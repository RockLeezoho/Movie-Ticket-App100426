package com.example.movie_ticket_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.adapters.SeatAdapter;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.models.Payment;
import com.example.movie_ticket_app.models.Showtime;
import com.example.movie_ticket_app.models.Ticket;
import com.example.movie_ticket_app.utils.BookingNotificationHelper;
import com.example.movie_ticket_app.utils.ReminderScheduler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Map;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_TAB = "selected_tab_id";
    public static final int TAB_TICKETS = R.id.nav_tickets;

    private TextView movieTitleView;
    private TextView theaterView;
    private TextView timeView;
    private TextView amountView;
    private TextView statusView;
    private TextView selectedSeatView;
    private RadioGroup paymentMethodGroup;
    private Button payButton;
    private ProgressBar progressBar;
    private RecyclerView seatRecyclerView;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private boolean isProcessing;
    private SeatAdapter seatAdapter;
    private final List<String> allSeats = new ArrayList<>();
    private final List<String> availableSeats = new ArrayList<>();
    private final List<String> bookedSeats = new ArrayList<>();
    private String selectedSeat;

    private String movieId;
    private String showtimeId;
    private String movieTitle;
    private String theaterName;
    private String showtimeTime;
    private long showtimeMillis;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        databaseReference = FirebaseDb.getRootReference();
        auth = FirebaseAuth.getInstance();

        movieTitleView = findViewById(R.id.payment_movie_title);
        theaterView = findViewById(R.id.payment_theater);
        timeView = findViewById(R.id.payment_time);
        amountView = findViewById(R.id.payment_amount);
        statusView = findViewById(R.id.payment_status_text);
        selectedSeatView = findViewById(R.id.payment_selected_seat);
        seatRecyclerView = findViewById(R.id.recycler_available_seats);
        paymentMethodGroup = findViewById(R.id.payment_method_group);
        payButton = findViewById(R.id.pay_button);
        progressBar = findViewById(R.id.payment_progress);

        seatAdapter = new SeatAdapter(availableSeats, seatCode -> {
            selectedSeat = seatCode;
            selectedSeatView.setText("Ghế đã chọn: " + seatCode);
        });
        seatRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        seatRecyclerView.setAdapter(seatAdapter);

        bindIntentData();
        loadAvailableSeats();
        payButton.setOnClickListener(v -> processPayment());
    }

    private void bindIntentData() {
        movieId = getIntent().getStringExtra("movie_id");
        showtimeId = getIntent().getStringExtra("showtime_id");
        movieTitle = getIntent().getStringExtra("movie_title");
        theaterName = getIntent().getStringExtra("theater_name");
        showtimeTime = getIntent().getStringExtra("showtime_time");
        showtimeMillis = getIntent().getLongExtra("showtime_start_millis", 0L);
        amount = getIntent().getDoubleExtra("showtime_price", 0.0);

        movieTitleView.setText(movieTitle == null ? "Phim" : movieTitle);
        theaterView.setText(theaterName == null ? "Rạp" : theaterName);
        timeView.setText(showtimeTime == null ? "Giờ chiếu" : showtimeTime);
        amountView.setText(String.format(Locale.getDefault(), "$%.2f", amount));
    }

    private void loadAvailableSeats() {
        if (showtimeId == null || showtimeId.isEmpty()) {
            isProcessing = false;
            payButton.setEnabled(false);
            progressBar.setVisibility(android.view.View.GONE);
            statusView.setText("Thiếu thông tin suất chiếu.");
            return;
        }

        isProcessing = true;
        payButton.setEnabled(false);
        progressBar.setVisibility(android.view.View.VISIBLE);
        statusView.setText("Đang tải ghế còn trống...");
        databaseReference.child(FirebasePaths.SHOWTIMES)
                .child(showtimeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookedSeats.clear();
                        Showtime showtime = snapshot.getValue(Showtime.class);
                        if (showtime != null && showtime.getBookedSeats() != null) {
                            for (String seat : showtime.getBookedSeats()) {
                                if (!TextUtils.isEmpty(seat)) {
                                    bookedSeats.add(seat.trim().toUpperCase(Locale.US));
                                }
                            }
                        }

                        allSeats.clear();
                        availableSeats.clear();
                        for (char row = 'A'; row <= 'E'; row++) {
                            for (int seatNumber = 1; seatNumber <= 10; seatNumber++) {
                                String seatCode = row + String.valueOf(seatNumber);
                                allSeats.add(seatCode);
                                if (!bookedSeats.contains(seatCode)) {
                                    availableSeats.add(seatCode);
                                }
                            }
                        }

                        seatAdapter.setBookedSeats(bookedSeats);
                        seatAdapter.notifyDataSetChanged();
                        if (!availableSeats.isEmpty()) {
                            selectedSeat = availableSeats.get(0);
                            seatAdapter.setSelectedSeat(selectedSeat);
                            selectedSeatView.setText("Ghế đã chọn: " + selectedSeat);
                            statusView.setText("Hãy chọn một ghế còn trống để tiếp tục thanh toán.");
                        } else {
                            selectedSeat = null;
                            selectedSeatView.setText("Ghế đã chọn: chưa có");
                            statusView.setText("Không còn ghế trống cho suất chiếu này.");
                        }
                        isProcessing = false;
                        progressBar.setVisibility(android.view.View.GONE);
                        payButton.setEnabled(!availableSeats.isEmpty());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isProcessing = false;
                        progressBar.setVisibility(android.view.View.GONE);
                        payButton.setEnabled(false);
                        statusView.setText("Không tải được ghế.");
                        Toast.makeText(PaymentActivity.this, "Không thể tải sơ đồ ghế.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processPayment() {
        if (isProcessing) {
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập trước.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedSeat)) {
            Toast.makeText(this, "Vui lòng chọn một ghế còn trống.", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = paymentMethodGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedMethod = findViewById(checkedId);
        String paymentMethod = selectedMethod.getText().toString();

        if (!availableSeats.contains(selectedSeat)) {
            Toast.makeText(this, "Ghế này đã được đặt.", Toast.LENGTH_SHORT).show();
            selectedSeat = availableSeats.isEmpty() ? null : availableSeats.get(0);
            if (selectedSeat != null) {
                seatAdapter.setSelectedSeat(selectedSeat);
                selectedSeatView.setText("Ghế đã chọn: " + selectedSeat);
            }
            return;
        }

        setProcessingState(true, "Đang xử lý giao dịch sandbox...");
        runSandboxPayment(selectedSeat, paymentMethod);
    }

    private void runSandboxPayment(String seatNumber, String paymentMethod) {
        setProcessingState(true, "Đang xử lý giao dịch sandbox...");
        long delayMillis = 1500L + random.nextInt(1200);
        uiHandler.postDelayed(() -> {
            boolean success = random.nextInt(100) < 85;
            if (success) {
                saveSuccessfulPayment(seatNumber, paymentMethod);
            } else {
                saveFailedPayment(seatNumber, paymentMethod);
            }
        }, delayMillis);
    }

    private void saveSuccessfulPayment(String seatNumber, String paymentMethod) {
        String ticketId = databaseReference.child(FirebasePaths.TICKETS).push().getKey();
        String paymentId = databaseReference.child(FirebasePaths.PAYMENTS).push().getKey();

        if (ticketId == null || paymentId == null) {
            setProcessingState(false, "Không thể tạo bản ghi đặt vé.");
            Toast.makeText(this, "Không thể tạo bản ghi đặt vé.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        long now = System.currentTimeMillis();

        Ticket ticket = new Ticket(
                ticketId,
                userId,
                showtimeId,
                paymentId,
                movieTitle == null ? "Phim" : movieTitle,
                theaterName == null ? "Rạp" : theaterName,
                showtimeTime == null ? "Giờ chiếu" : showtimeTime,
                seatNumber,
                amount,
                showtimeMillis
        );

        Payment payment = new Payment(
                paymentId,
                userId,
                ticketId,
                showtimeId,
                movieTitle == null ? "Movie" : movieTitle,
                theaterName == null ? "Theater" : theaterName,
                seatNumber,
                paymentMethod,
                "HOÀN THÀNH",
                amount,
                now
        );

        Map<String, Object> updates = new HashMap<>();
        updates.put(FirebasePaths.TICKETS + "/" + ticketId, ticket);
        updates.put(FirebasePaths.PAYMENTS + "/" + paymentId, payment);
        updates.put(FirebasePaths.SHOWTIMES + "/" + showtimeId + "/bookedSeats", buildUpdatedBookedSeats(ticket.getSeatNumber()));

        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                setProcessingState(false, "Không thể lưu thanh toán.");
                Toast.makeText(this, "Thanh toán thất bại.", Toast.LENGTH_SHORT).show();
                return;
            }

            BookingNotificationHelper.showSuccess(
                    this,
                    "Vé của bạn cho phim " + ticket.getMovieTitle() + " đã được xác nhận. Ghế " + ticket.getSeatNumber() + " đã được đặt."
            );
            ReminderScheduler.showNow(this, ticket);
            ReminderScheduler.schedule(this, ticket);
            setProcessingState(false, "Thanh toán sandbox đã được duyệt.");
            Toast.makeText(this, "Thanh toán thành công. Vé đã được lưu vào Firebase.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, PaymentDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(PaymentDetailActivity.EXTRA_PAYMENT_ID, paymentId);
            startActivity(intent);
            finish();
        });
    }

    private void saveFailedPayment(String seatNumber, String paymentMethod) {
        String paymentId = databaseReference.child(FirebasePaths.PAYMENTS).push().getKey();
        if (paymentId == null) {
            setProcessingState(false, "Không thể tạo bản ghi thanh toán lỗi.");
            Toast.makeText(this, "Không thể tạo bản ghi thanh toán.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        long now = System.currentTimeMillis();

        Payment payment = new Payment(
                paymentId,
                userId,
                null,
                showtimeId,
            movieTitle == null ? "Phim" : movieTitle,
            theaterName == null ? "Rạp" : theaterName,
                seatNumber,
                paymentMethod,
                "THẤT BẠI",
                amount,
                now
        );

        databaseReference.child(FirebasePaths.PAYMENTS).child(paymentId).setValue(payment)
                .addOnCompleteListener(task -> {
                    setProcessingState(false, "Thanh toán sandbox bị từ chối.");
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Thanh toán sandbox thất bại. Bạn có thể thử lại.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Không thể lưu bản ghi thanh toán.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setProcessingState(boolean processing, String message) {
        isProcessing = processing;
        payButton.setEnabled(!processing);
        progressBar.setVisibility(processing ? android.view.View.VISIBLE : android.view.View.GONE);
        statusView.setText(message);
    }

    private List<String> buildUpdatedBookedSeats(String newSeatNumber) {
        List<String> updatedSeats = new ArrayList<>(bookedSeats);
        if (!TextUtils.isEmpty(newSeatNumber)) {
            String normalizedSeat = newSeatNumber.trim().toUpperCase(Locale.US);
            if (!updatedSeats.contains(normalizedSeat)) {
                updatedSeats.add(normalizedSeat);
            }
        }
        return updatedSeats;
    }

}