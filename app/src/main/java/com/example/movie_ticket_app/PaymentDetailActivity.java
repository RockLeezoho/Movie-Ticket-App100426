package com.example.movie_ticket_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Payment;
import com.example.movie_ticket_app.models.Ticket;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Locale;

public class PaymentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_ID = "payment_id";

    private TextView titleView;
    private TextView statusView;
    private TextView paymentIdView;
    private TextView ticketIdView;
    private TextView movieView;
    private TextView theaterView;
    private TextView seatView;
    private TextView methodView;
    private TextView amountView;
    private TextView timeView;
    private TextView ticketInfoView;
    private MaterialButton backButton;
    private MaterialButton viewTicketButton;
    private MaterialButton viewHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        titleView = findViewById(R.id.payment_detail_title);
        statusView = findViewById(R.id.payment_detail_status);
        paymentIdView = findViewById(R.id.payment_detail_payment_id);
        ticketIdView = findViewById(R.id.payment_detail_ticket_id);
        movieView = findViewById(R.id.payment_detail_movie);
        theaterView = findViewById(R.id.payment_detail_theater);
        seatView = findViewById(R.id.payment_detail_seat);
        methodView = findViewById(R.id.payment_detail_method);
        amountView = findViewById(R.id.payment_detail_amount);
        timeView = findViewById(R.id.payment_detail_time);
        ticketInfoView = findViewById(R.id.payment_detail_ticket_info);
        backButton = findViewById(R.id.btn_back_payment_detail);
        viewTicketButton = findViewById(R.id.btn_view_ticket);
        viewHistoryButton = findViewById(R.id.btn_open_history);

        backButton.setOnClickListener(v -> finish());
        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentHistoryActivity.class);
            startActivity(intent);
        });

        String paymentId = getIntent().getStringExtra(EXTRA_PAYMENT_ID);
        if (paymentId == null || paymentId.trim().isEmpty()) {
            Toast.makeText(this, "Thiếu mã thanh toán.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPaymentDetail(paymentId);
    }

    private void loadPaymentDetail(String paymentId) {
        FirebaseDb.getRootReference()
                .child(FirebasePaths.PAYMENTS)
                .child(paymentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Payment payment = snapshot.getValue(Payment.class);
                        if (payment == null) {
                            Toast.makeText(PaymentDetailActivity.this, "Không tìm thấy thanh toán.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        if (FirebaseAuth.getInstance().getCurrentUser() == null
                                || !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(payment.getUserId())) {
                            Toast.makeText(PaymentDetailActivity.this, "Không có quyền truy cập.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        bindPayment(payment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PaymentDetailActivity.this, "Không tải được thanh toán.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void bindPayment(Payment payment) {
        titleView.setText("Chi tiết giao dịch");
        statusView.setText(payment.getStatus());
        paymentIdView.setText(payment.getId());
        ticketIdView.setText(payment.getTicketId() == null ? "Chưa phát hành vé" : payment.getTicketId());
        movieView.setText(payment.getMovieTitle());
        theaterView.setText(payment.getTheaterName());
        seatView.setText(String.format(Locale.getDefault(), "Ghế: %s", payment.getSeatNumber()));
        methodView.setText(payment.getPaymentMethod());
        amountView.setText(String.format(Locale.getDefault(), "$%.2f", payment.getAmount()));
        timeView.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(payment.getCreatedAtMillis()));

        if (payment.getTicketId() == null || payment.getTicketId().trim().isEmpty()) {
            ticketInfoView.setText("Giao dịch này không phát hành vé vì đã bị từ chối ở chế độ sandbox.");
            viewTicketButton.setVisibility(View.GONE);
        } else {
            ticketInfoView.setText("Vé đã được phát hành thành công và lưu vào Firebase.");
            viewTicketButton.setVisibility(View.VISIBLE);
            viewTicketButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, TicketDetailActivity.class);
                intent.putExtra(TicketDetailActivity.EXTRA_TICKET_ID, payment.getTicketId());
                startActivity(intent);
            });
            loadTicket(payment.getTicketId());
        }
    }

    private void loadTicket(String ticketId) {
        FirebaseDb.getRootReference()
                .child(FirebasePaths.TICKETS)
                .child(ticketId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Ticket ticket = snapshot.getValue(Ticket.class);
                        if (ticket == null) {
                            return;
                        }

                        ticketInfoView.setText(String.format(Locale.getDefault(),
                            "Vé %s đã được phát hành cho phim %s, %s tại ghế %s.",
                                ticket.getId(),
                                ticket.getMovieTitle(),
                                ticket.getTime(),
                                ticket.getSeatNumber()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}