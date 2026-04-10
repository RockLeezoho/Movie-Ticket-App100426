package com.example.movie_ticket_app;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movie_ticket_app.adapters.ShowtimeAdapter;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.Showtime;
import com.example.movie_ticket_app.models.Showtime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView poster;
    private TextView title;
    private TextView genre;
    private TextView rating;
    private TextView description;
    private RecyclerView recyclerShowtimes;

    private final List<Showtime> showtimeList = new ArrayList<>();
    private ShowtimeAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mDatabase = FirebaseDb.getRootReference();
        mAuth = FirebaseAuth.getInstance();

        poster = findViewById(R.id.detail_poster);
        title = findViewById(R.id.detail_title);
        genre = findViewById(R.id.detail_genre);
        rating = findViewById(R.id.detail_rating);
        description = findViewById(R.id.detail_description);
        recyclerShowtimes = findViewById(R.id.recycler_showtimes);

        adapter = new ShowtimeAdapter(showtimeList, this::openPaymentScreen);
        recyclerShowtimes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerShowtimes.setAdapter(adapter);

        bindMovieHeader();
        loadMovieShowtimes();
    }

    private void bindMovieHeader() {
        movieId = getIntent().getStringExtra("movie_id");

        String movieTitle = getIntent().getStringExtra("movie_title");
        String movieGenre = getIntent().getStringExtra("movie_genre");
        String movieDescription = getIntent().getStringExtra("movie_description");
        double movieRating = getIntent().getDoubleExtra("movie_rating", 0);
        String posterUrl = getIntent().getStringExtra("movie_poster");

        title.setText(movieTitle == null ? "Movie Details" : movieTitle);
        genre.setText(movieGenre == null ? "Genre" : movieGenre);
        description.setText(movieDescription == null ? "Description is being updated." : movieDescription);
        rating.setText(String.format(Locale.getDefault(), "★ %.1f", movieRating));

        Glide.with(this)
                .load(posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(poster);
    }

    private void loadMovieShowtimes() {
        if (movieId == null || movieId.isEmpty()) {
            Toast.makeText(this, "Missing movie data.", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(FirebasePaths.SHOWTIMES)
                .orderByChild("movieId")
                .equalTo(movieId)
                .get()
                .addOnSuccessListener(this::bindShowtimes)
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load showtimes", Toast.LENGTH_SHORT).show());
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

        if (showtimeList.isEmpty()) {
            Toast.makeText(this, "No showtimes available for this movie", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPaymentScreen(Showtime showtime) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to book tickets", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("movie_id", movieId);
        intent.putExtra("showtime_id", showtime.getId());
        intent.putExtra("movie_title", title.getText().toString());
        intent.putExtra("theater_name", showtime.getTheaterName() == null ? "Theater" : showtime.getTheaterName());
        intent.putExtra("showtime_time", showtime.getTime());
        intent.putExtra("showtime_price", showtime.getPrice());
        intent.putExtra("showtime_start_millis", showtime.getStartTimeMillis());
        startActivity(intent);
    }
}
