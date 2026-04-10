package com.example.movie_ticket_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.movie_ticket_app.data.SampleDataSeeder;
import com.example.movie_ticket_app.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private final List<Movie> movieList = new ArrayList<>();
    private MovieAdapter adapter;
    private TextView emptyState;
    private boolean hasSeedRetried;

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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            emptyState.setText("Vui lòng đăng nhập để xem danh sách phim.");
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        currentUser.getIdToken(true).addOnCompleteListener(tokenTask ->
                FirebaseDb.getRootReference()
                        .child(FirebasePaths.MOVIES)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                bindMovies(snapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                emptyState.setText("Không thể tải danh sách phim.");
                                emptyState.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Không thể tải danh sách phim.", Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    private void bindMovies(DataSnapshot snapshot) {
        movieList.clear();
        for (DataSnapshot child : snapshot.getChildren()) {
            Movie movie = child.getValue(Movie.class);
            if (movie != null) {
                movieList.add(movie);
            }
        }

        if (movieList.isEmpty() && !hasSeedRetried) {
            hasSeedRetried = true;
            emptyState.setText("Đang tạo lại dữ liệu phim mẫu...");
            emptyState.setVisibility(View.VISIBLE);
            SampleDataSeeder.seedIfNeeded(this::loadMovies);
            return;
        }

        adapter.notifyDataSetChanged();
        emptyState.setVisibility(movieList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
