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
import com.example.movie_ticket_app.adapters.TheaterAdapter;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Theater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TheatersFragment extends Fragment {

    private final List<Theater> theaterList = new ArrayList<>();
    private TheaterAdapter adapter;
    private TextView emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theaters, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_theaters);
        emptyState = view.findViewById(R.id.text_empty_theaters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TheaterAdapter(theaterList);
        recyclerView.setAdapter(adapter);

        loadTheaters();
        return view;
    }

    private void loadTheaters() {
        FirebaseDb.getRootReference()
                .child(FirebasePaths.THEATERS)
                .get()
                .addOnSuccessListener(this::bindTheaters)
                .addOnFailureListener(e -> emptyState.setVisibility(View.VISIBLE));
    }

    private void bindTheaters(DataSnapshot snapshot) {
        theaterList.clear();
        for (DataSnapshot child : snapshot.getChildren()) {
            Theater theater = child.getValue(Theater.class);
            if (theater != null) {
                theaterList.add(theater);
            }
        }

        adapter.notifyDataSetChanged();
        emptyState.setVisibility(theaterList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
