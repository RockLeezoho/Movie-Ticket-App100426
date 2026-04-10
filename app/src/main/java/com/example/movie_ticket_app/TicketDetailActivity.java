package com.example.movie_ticket_app;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Ticket;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "ticket_id";

    private TextView ticketIdView;
    private TextView movieView;
    private TextView theaterView;
    private TextView timeView;
    private TextView seatView;
    private TextView paymentIdView;
    private TextView priceView;
    private TextView showtimeView;
    private TextView statusView;
    private MaterialButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ticketIdView = findViewById(R.id.ticket_detail_id);
        movieView = findViewById(R.id.ticket_detail_movie);
        theaterView = findViewById(R.id.ticket_detail_theater);
        timeView = findViewById(R.id.ticket_detail_time);
        seatView = findViewById(R.id.ticket_detail_seat);
        paymentIdView = findViewById(R.id.ticket_detail_payment_id);
        priceView = findViewById(R.id.ticket_detail_price);
        showtimeView = findViewById(R.id.ticket_detail_showtime);
        statusView = findViewById(R.id.ticket_detail_status);
        backButton = findViewById(R.id.btn_back_ticket_detail);

        backButton.setOnClickListener(v -> finish());

        String ticketId = getIntent().getStringExtra(EXTRA_TICKET_ID);
        if (ticketId == null || ticketId.trim().isEmpty()) {
            Toast.makeText(this, "Missing ticket id.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTicket(ticketId);
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
                            Toast.makeText(TicketDetailActivity.this, "Ticket not found.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        if (FirebaseAuth.getInstance().getCurrentUser() == null
                                || !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ticket.getUserId())) {
                            Toast.makeText(TicketDetailActivity.this, "Unauthorized.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        bindTicket(ticket);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TicketDetailActivity.this, "Failed to load ticket.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void bindTicket(Ticket ticket) {
        ticketIdView.setText(ticket.getId());
        movieView.setText(ticket.getMovieTitle());
        theaterView.setText(ticket.getTheaterName());
        timeView.setText(ticket.getTime());
        seatView.setText(String.format(Locale.getDefault(), "Seat: %s", ticket.getSeatNumber()));
        paymentIdView.setText(ticket.getPaymentId() == null ? "No payment id" : ticket.getPaymentId());
        priceView.setText(String.format(Locale.getDefault(), "$%.2f", ticket.getPrice()));
        showtimeView.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(ticket.getShowtimeMillis()));
        statusView.setText("Confirmed");
    }
}