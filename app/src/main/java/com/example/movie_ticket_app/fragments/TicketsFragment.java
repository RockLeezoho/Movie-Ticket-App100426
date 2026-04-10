package com.example.movie_ticket_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.adapters.TicketAdapter;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment {

    private final List<Ticket> ticketList = new ArrayList<>();
    private TicketAdapter adapter;
    private TextView emptyState;
    private boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_tickets);
        emptyState = view.findViewById(R.id.text_empty_tickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TicketAdapter(ticketList);
        recyclerView.setAdapter(adapter);

        loadTickets();
        return view;
    }

    private void loadTickets() {
        if (isLoading) {
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            emptyState.setText("Vui lòng đăng nhập để xem vé đã đặt.");
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        isLoading = true;
        emptyState.setText("Đang tải vé đã đặt...");
        emptyState.setVisibility(View.VISIBLE);

        currentUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
            String userId = currentUser.getUid();
            FirebaseDb.getRootReference()
                    .child(FirebasePaths.TICKETS)
                    .orderByChild("userId")
                    .equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ticketList.clear();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Ticket ticket = child.getValue(Ticket.class);
                                if (ticket != null) {
                                    ticketList.add(ticket);
                                }
                            }

                            adapter.notifyDataSetChanged();
                            isLoading = false;
                            if (ticketList.isEmpty()) {
                                emptyState.setText("Bạn chưa đặt vé nào.");
                                emptyState.setVisibility(View.VISIBLE);
                            } else {
                                emptyState.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            isLoading = false;
                            emptyState.setText("Không thể tải danh sách vé.");
                            emptyState.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Không thể tải danh sách vé.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
