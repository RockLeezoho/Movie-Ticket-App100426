package com.example.movie_ticket_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.TextView;

import com.example.movie_ticket_app.data.SampleDataSeeder;
import com.example.movie_ticket_app.fragments.MoviesFragment;
import com.example.movie_ticket_app.fragments.ProfileFragment;
import com.example.movie_ticket_app.fragments.ShowtimesFragment;
import com.example.movie_ticket_app.fragments.TheatersFragment;
import com.example.movie_ticket_app.fragments.TicketsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        View fragmentContainer = findViewById(R.id.fragment_container);
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.INVISIBLE);
        }

        TextView loadingText = new TextView(this);
        loadingText.setText("Loading demo data...");
        loadingText.setTextSize(18f);
        loadingText.setPadding(48, 48, 48, 48);
        loadingText.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        ((androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.main)).addView(loadingText);

        refreshAuthTokenThenSeed(() -> runOnUiThread(() -> {
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.VISIBLE);
            }
            ((androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.main)).removeView(loadingText);
            SampleDataSeeder.seedDemoUserBookingsIfNeeded();
            requestNotificationPermissionIfNeeded();

            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_movies) {
                    selectedFragment = new MoviesFragment();
                } else if (id == R.id.nav_theaters) {
                    selectedFragment = new TheatersFragment();
                } else if (id == R.id.nav_showtimes) {
                    selectedFragment = new ShowtimesFragment();
                } else if (id == R.id.nav_tickets) {
                    selectedFragment = new TicketsFragment();
                } else if (id == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            });

            if (savedInstanceState == null) {
                int selectedTabId = getIntent().getIntExtra(PaymentActivity.EXTRA_SELECTED_TAB, R.id.nav_movies);
                bottomNav.setSelectedItemId(selectedTabId);
            }
        }));
    }

    private void refreshAuthTokenThenSeed(Runnable onReady) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            SampleDataSeeder.seedIfNeeded(onReady);
            return;
        }

        currentUser.getIdToken(true).addOnCompleteListener(tokenTask -> SampleDataSeeder.seedIfNeeded(onReady));
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }
}
