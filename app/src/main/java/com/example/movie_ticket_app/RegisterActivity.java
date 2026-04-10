package com.example.movie_ticket_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.example.movie_ticket_app.models.User;
import com.example.movie_ticket_app.utils.AuthNotificationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDb.getRootReference();

        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button registerButton = findViewById(R.id.register_button);
        TextView loginText = findViewById(R.id.login_text);

        registerButton.setOnClickListener(v -> registerUser());
        loginText.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = String.valueOf(nameEditText.getText()).trim();
        String email = String.valueOf(emailEditText.getText()).trim();
        String password = String.valueOf(passwordEditText.getText()).trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_name_required));
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_email_required));
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_password_required));
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError(getString(R.string.error_password_min_length));
            passwordEditText.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful() || mAuth.getCurrentUser() == null) {
                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.auth_register_failed);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    firebaseUser.updateProfile(profileUpdate).addOnCompleteListener(profileTask -> {
                        if (!profileTask.isSuccessful()) {
                            Toast.makeText(this, getString(R.string.auth_profile_update_failed), Toast.LENGTH_SHORT).show();
                        }

                        firebaseUser.getIdToken(true).addOnCompleteListener(tokenTask -> writeUserProfile(firebaseUser, userId, email, name));
                    });
                });
    }

    private void writeUserProfile(FirebaseUser firebaseUser, String userId, String email, String name) {
        User user = new User(userId, email, name);
        mDatabase.child(FirebasePaths.USERS).child(userId).setValue(user)
                .addOnCompleteListener(dbTask -> {
                    Toast.makeText(this, getString(R.string.auth_register_success), Toast.LENGTH_SHORT).show();
                    AuthNotificationHelper.showSuccess(this, getString(R.string.notification_register_success_body));
                    if (!dbTask.isSuccessful()) {
                        String dbMessage = getFirebaseErrorMessage(dbTask.getException());
                        Toast.makeText(this, dbMessage, Toast.LENGTH_LONG).show();
                    }

                    // The auth account is already created, so let the user into the app even if
                    // the profile sync is blocked by remote rules or a temporary network issue.
                    firebaseUser.getIdToken(true).addOnCompleteListener(tokenTask ->
                            uiHandler.postDelayed(this::openMainScreen, 500));
                });
    }

    private String getFirebaseErrorMessage(Exception exception) {
        if (exception == null) {
            return getString(R.string.auth_profile_sync_failed);
        }

        if (exception instanceof com.google.firebase.database.DatabaseException) {
            return exception.getMessage();
        }

        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return getString(R.string.auth_profile_sync_failed);
        }

        if (message.toLowerCase().contains("permission denied")) {
            return "Firebase Database permission denied. Check that Realtime Database rules are deployed for the current project.";
        }

        return message;
    }

    private void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
