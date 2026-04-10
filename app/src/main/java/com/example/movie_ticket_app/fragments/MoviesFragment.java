package com.example.movie_ticket_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.MovieDetailsActivity;
import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.adapters.MovieAdapter;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.models.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private final List<Movie> movieList = new ArrayList<>();
    private MovieAdapter adapter;
    private TextView emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_movies);
        emptyState = view.findViewById(R.id.text_empty_movies);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new MovieAdapter(movieList, movie -> {
            Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("movie_title", movie.getTitle());
            intent.putExtra("movie_description", movie.getDescription());
            intent.putExtra("movie_genre", movie.getGenre());
            intent.putExtra("movie_rating", movie.getRating());
            intent.putExtra("movie_poster", movie.getPosterUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadMovies();
        return view;
    }

    private void loadMovies() {
        FirebaseDb.getRootReference()
                .child(FirebasePaths.MOVIES)
                .get()
                .addOnSuccessListener(this::bindMovies)
                .addOnFailureListener(e -> emptyState.setVisibility(View.VISIBLE));
    }

    private void bindMovies(DataSnapshot snapshot) {
        movieList.clear();
        for (DataSnapshot child : snapshot.getChildren()) {
            Movie movie = child.getValue(Movie.class);
            if (movie != null) {
                movieList.add(movie);
            }
        }

        adapter.notifyDataSetChanged();
        emptyState.setVisibility(movieList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
