package com.example.movie_ticket_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.data.SampleDataSeeder;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DebugFirebaseActivity extends AppCompatActivity {

    private TextView connectionValue;
    private TextView authValue;
    private TextView databaseValue;
    private TextView moviesValue;
    private TextView theatersValue;
    private TextView showtimesValue;
    private TextView ticketsValue;
    private TextView paymentsValue;

    private DatabaseReference connectionRef;
    private ValueEventListener connectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_firebase);

        connectionValue = findViewById(R.id.debug_connection_value);
        authValue = findViewById(R.id.debug_auth_value);
        databaseValue = findViewById(R.id.debug_database_value);
        moviesValue = findViewById(R.id.debug_movies_value);
        theatersValue = findViewById(R.id.debug_theaters_value);
        showtimesValue = findViewById(R.id.debug_showtimes_value);
        ticketsValue = findViewById(R.id.debug_tickets_value);
        paymentsValue = findViewById(R.id.debug_payments_value);

        MaterialButton backBtn = findViewById(R.id.btn_back_debug);
        MaterialButton refreshBtn = findViewById(R.id.btn_refresh_debug);
        MaterialButton forceSeedBtn = findViewById(R.id.btn_force_seed);

        backBtn.setOnClickListener(v -> finish());
        refreshBtn.setOnClickListener(v -> refreshDebugState());
        forceSeedBtn.setOnClickListener(v -> forceSeedDemoData());

        databaseValue.setText("CSDL: " + FirebaseDb.getDatabaseUrl());
        attachConnectionListener();
        refreshDebugState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionRef != null && connectionListener != null) {
            connectionRef.removeEventListener(connectionListener);
        }
    }

    private void attachConnectionListener() {
        connectionRef = FirebaseDb.getDatabase().getReference(".info/connected");
        connectionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(connected)) {
                    connectionValue.setText("Kết nối: trực tuyến");
                } else {
                    connectionValue.setText("Kết nối: ngoại tuyến");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                connectionValue.setText("Kết nối: lỗi - " + sanitize(error.getMessage()));
            }
        };
        connectionRef.addValueEventListener(connectionListener);
    }

    private void refreshDebugState() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            authValue.setText("Xác thực: chưa đăng nhập");
            ticketsValue.setText("Vé: cần đăng nhập");
            paymentsValue.setText("Thanh toán: cần đăng nhập");
            loadCollectionCount(FirebasePaths.MOVIES, moviesValue, "Phim");
            loadCollectionCount(FirebasePaths.THEATERS, theatersValue, "Rạp");
            loadCollectionCount(FirebasePaths.SHOWTIMES, showtimesValue, "Suất chiếu");
            return;
        } else {
            String email = user.getEmail();
            String uid = user.getUid();
            String shortUid = uid.length() > 10 ? uid.substring(0, 10) + "..." : uid;
            String userLabel = TextUtils.isEmpty(email) ? shortUid : email + " (" + shortUid + ")";
            authValue.setText("Xác thực: đã đăng nhập với " + userLabel);
            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                if (!tokenTask.isSuccessful()) {
                    authValue.setText("Xác thực: đã đăng nhập nhưng làm mới token thất bại: " + sanitize(tokenTask.getException() == null ? null : tokenTask.getException().getMessage()));
                }
                loadUserScopedCount(FirebasePaths.TICKETS, user.getUid(), ticketsValue, "Vé");
                loadUserScopedCount(FirebasePaths.PAYMENTS, user.getUid(), paymentsValue, "Thanh toán");
                loadCollectionCount(FirebasePaths.MOVIES, moviesValue, "Phim");
                loadCollectionCount(FirebasePaths.THEATERS, theatersValue, "Rạp");
                loadCollectionCount(FirebasePaths.SHOWTIMES, showtimesValue, "Suất chiếu");
            });
        }
    }

    private void loadCollectionCount(String path, TextView targetView, String label) {
        DatabaseReference reference = FirebaseDb.getRootReference().child(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                targetView.setText(label + ": " + snapshot.getChildrenCount() + " node");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                targetView.setText(label + ": bị chặn - " + sanitize(error.getMessage()) + " (mã " + error.getCode() + ")");
            }
        });
    }

    private void loadUserScopedCount(String path, String userId, TextView targetView, String label) {
        Query query = FirebaseDb.getRootReference().child(path).orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                targetView.setText(label + ": " + snapshot.getChildrenCount() + " node cho người dùng hiện tại");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                targetView.setText(label + ": bị chặn - " + sanitize(error.getMessage()) + " (mã " + error.getCode() + ")");
            }
        });
    }

    private void forceSeedDemoData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập trước.", Toast.LENGTH_SHORT).show();
            return;
        }

        user.getIdToken(true).addOnCompleteListener(tokenTask ->
                SampleDataSeeder.seedIfNeeded(() -> {
                    Toast.makeText(this, "Đã kích hoạt tạo dữ liệu mẫu.", Toast.LENGTH_SHORT).show();
                    refreshDebugState();
                }));
    }

    private String sanitize(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "lỗi không xác định";
        }
        return message.trim();
    }
}