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
import com.example.movie_ticket_app.adapters.GlobalShowtimeAdapter;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Showtime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowtimesFragment extends Fragment {
    private final List<Showtime> showtimeList = new ArrayList<>();
    private GlobalShowtimeAdapter adapter;
    private TextView emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showtimes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_all_showtimes);
        emptyState = view.findViewById(R.id.text_empty_showtimes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GlobalShowtimeAdapter(showtimeList);
        recyclerView.setAdapter(adapter);

        loadShowtimes();
        return view;
    }

    private void loadShowtimes() {
        FirebaseDb.getRootReference()
                .child(FirebasePaths.SHOWTIMES)
                .get()
                .addOnSuccessListener(this::bindShowtimes)
                .addOnFailureListener(e -> emptyState.setVisibility(View.VISIBLE));
    }

    private void bindShowtimes(DataSnapshot snapshot) {
        showtimeList.clear();
        for (DataSnapshot child : snapshot.getChildren()) {
            Showtime showtime = child.getValue(Showtime.class);
            if (showtime != null) {
                showtimeList.add(showtime);
            }
        }

        showtimeList.sort(Comparator.comparingLong(Showtime::getStartTimeMillis));
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(showtimeList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}

