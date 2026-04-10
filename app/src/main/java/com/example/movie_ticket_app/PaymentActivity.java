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
            selectedSeatView.setText("Selected seat: " + seatCode);
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

        movieTitleView.setText(movieTitle == null ? "Movie" : movieTitle);
        theaterView.setText(theaterName == null ? "Theater" : theaterName);
        timeView.setText(showtimeTime == null ? "Time" : showtimeTime);
        amountView.setText(String.format(Locale.getDefault(), "$%.2f", amount));
    }

    private void loadAvailableSeats() {
        if (showtimeId == null || showtimeId.isEmpty()) {
            isProcessing = false;
            payButton.setEnabled(false);
            progressBar.setVisibility(android.view.View.GONE);
            statusView.setText("Missing showtime information.");
            return;
        }

        isProcessing = true;
        payButton.setEnabled(false);
        progressBar.setVisibility(android.view.View.VISIBLE);
        statusView.setText("Loading available seats...");
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

                        availableSeats.clear();
                        for (char row = 'A'; row <= 'E'; row++) {
                            for (int seatNumber = 1; seatNumber <= 10; seatNumber++) {
                                String seatCode = row + String.valueOf(seatNumber);
                                if (!bookedSeats.contains(seatCode)) {
                                    availableSeats.add(seatCode);
                                }
                            }
                        }

                        seatAdapter.notifyDataSetChanged();
                        if (!availableSeats.isEmpty()) {
                            selectedSeat = availableSeats.get(0);
                            seatAdapter.setSelectedSeat(selectedSeat);
                            selectedSeatView.setText("Selected seat: " + selectedSeat);
                            statusView.setText("Choose an available seat and continue to payment.");
                        } else {
                            selectedSeat = null;
                            selectedSeatView.setText("Selected seat: none");
                            statusView.setText("No seats available for this showtime.");
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
                        statusView.setText("Failed to load seats.");
                        Toast.makeText(PaymentActivity.this, "Could not load seat map.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processPayment() {
        if (isProcessing) {
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedSeat)) {
            Toast.makeText(this, "Please select an available seat.", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = paymentMethodGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Choose a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedMethod = findViewById(checkedId);
        String paymentMethod = selectedMethod.getText().toString();

        if (!availableSeats.contains(selectedSeat)) {
            Toast.makeText(this, "This seat is already booked.", Toast.LENGTH_SHORT).show();
            selectedSeat = availableSeats.isEmpty() ? null : availableSeats.get(0);
            if (selectedSeat != null) {
                seatAdapter.setSelectedSeat(selectedSeat);
                selectedSeatView.setText("Selected seat: " + selectedSeat);
            }
            return;
        }

        setProcessingState(true, "Processing sandbox transaction...");
        runSandboxPayment(selectedSeat, paymentMethod);
    }

    private void runSandboxPayment(String seatNumber, String paymentMethod) {
        setProcessingState(true, "Processing sandbox transaction...");
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
            setProcessingState(false, "Could not create booking records.");
            Toast.makeText(this, "Could not create booking records.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        long now = System.currentTimeMillis();

        Ticket ticket = new Ticket(
                ticketId,
                userId,
                showtimeId,
                paymentId,
                movieTitle == null ? "Movie" : movieTitle,
                theaterName == null ? "Theater" : theaterName,
                showtimeTime == null ? "Time" : showtimeTime,
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
                "COMPLETED",
                amount,
                now
        );

        Map<String, Object> updates = new HashMap<>();
        updates.put(FirebasePaths.TICKETS + "/" + ticketId, ticket);
        updates.put(FirebasePaths.PAYMENTS + "/" + paymentId, payment);
        updates.put(FirebasePaths.SHOWTIMES + "/" + showtimeId + "/bookedSeats", buildUpdatedBookedSeats(ticket.getSeatNumber()));

        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                setProcessingState(false, "Payment failed to save.");
                Toast.makeText(this, "Payment failed.", Toast.LENGTH_SHORT).show();
                return;
            }

            BookingNotificationHelper.showSuccess(
                    this,
                    "Your ticket for " + ticket.getMovieTitle() + " is confirmed. Seat " + ticket.getSeatNumber() + " has been booked."
            );
            ReminderScheduler.schedule(this, ticket);
            setProcessingState(false, "Sandbox payment approved.");
            Toast.makeText(this, "Payment successful. Ticket saved to Firebase.", Toast.LENGTH_LONG).show();

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
            setProcessingState(false, "Could not create failed payment record.");
            Toast.makeText(this, "Could not create payment record.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        long now = System.currentTimeMillis();

        Payment payment = new Payment(
                paymentId,
                userId,
                null,
                showtimeId,
                movieTitle == null ? "Movie" : movieTitle,
                theaterName == null ? "Theater" : theaterName,
                seatNumber,
                paymentMethod,
                "FAILED",
                amount,
                now
        );

        databaseReference.child(FirebasePaths.PAYMENTS).child(paymentId).setValue(payment)
                .addOnCompleteListener(task -> {
                    setProcessingState(false, "Sandbox payment declined.");
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Sandbox payment failed. You can retry.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Failed to save payment record.", Toast.LENGTH_SHORT).show();
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