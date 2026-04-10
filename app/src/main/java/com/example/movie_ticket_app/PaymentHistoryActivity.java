package com.example.movie_ticket_app;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.adapters.PaymentHistoryAdapter;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Payment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {

    private final List<Payment> paymentList = new ArrayList<>();
    private PaymentHistoryAdapter adapter;
    private TextView emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        RecyclerView recyclerView = findViewById(R.id.recycler_payment_history);
        emptyState = findViewById(R.id.text_empty_payment_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PaymentHistoryAdapter(paymentList, payment -> {
            Intent intent = new Intent(this, PaymentDetailActivity.class);
            intent.putExtra(PaymentDetailActivity.EXTRA_PAYMENT_ID, payment.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_back_from_history).setOnClickListener(v -> finish());

        loadPayments();
    }

    private void loadPayments() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            emptyState.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDb.getRootReference()
                .child(FirebasePaths.PAYMENTS)
                .orderByChild("userId")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        paymentList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Payment payment = child.getValue(Payment.class);
                            if (payment != null) {
                                paymentList.add(payment);
                            }
                        }

                        paymentList.sort(Comparator.comparingLong(Payment::getCreatedAtMillis).reversed());
                        adapter.notifyDataSetChanged();
                        emptyState.setVisibility(paymentList.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });
    }
}