package com.example.movie_ticket_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDb.getRootReference()
                .child(FirebasePaths.TICKETS)
                .orderByChild("userId")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
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
                        emptyState.setVisibility(ticketList.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });
    }
}
