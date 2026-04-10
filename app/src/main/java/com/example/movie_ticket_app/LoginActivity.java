package com.example.movie_ticket_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.movie_ticket_app.utils.AuthNotificationHelper;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        registerText = findViewById(R.id.register_text);

        loginButton.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getString(R.string.auth_login_success), Toast.LENGTH_SHORT).show();
                        AuthNotificationHelper.showSuccess(LoginActivity.this, getString(R.string.notification_login_success_body));
                        FirebaseAuth currentAuth = FirebaseAuth.getInstance();
                        if (currentAuth.getCurrentUser() != null) {
                            currentAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(tokenTask -> {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            });
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
