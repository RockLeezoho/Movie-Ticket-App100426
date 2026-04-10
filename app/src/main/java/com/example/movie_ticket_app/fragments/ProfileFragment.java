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
import com.example.movie_ticket_app.DebugFirebaseActivity;
import com.example.movie_ticket_app.PaymentHistoryActivity;
import com.example.movie_ticket_app.LoginActivity;
import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.data.FirebaseDb;
import com.example.movie_ticket_app.data.FirebasePaths;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView nameText = view.findViewById(R.id.profile_name);
        TextView emailText = view.findViewById(R.id.profile_email);
        MaterialButton logoutBtn = view.findViewById(R.id.btn_logout);
        MaterialButton paymentHistoryBtn = view.findViewById(R.id.btn_payment_history);
        MaterialButton firebaseDebugBtn = view.findViewById(R.id.btn_firebase_debug);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference mDatabase;

        if (user != null) {
            emailText.setText(user.getEmail());
            nameText.setText(resolveFallbackName(user));
            mDatabase = FirebaseDb.getRootReference().child(FirebasePaths.USERS).child(user.getUid());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("fullName").getValue(String.class);
                        if (name != null && !name.trim().isEmpty()) {
                            nameText.setText(name);
                        } else {
                            nameText.setText(resolveFallbackName(user));
                        }
                    } else {
                        nameText.setText(resolveFallbackName(user));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        paymentHistoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PaymentHistoryActivity.class);
            startActivity(intent);
        });

        firebaseDebugBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DebugFirebaseActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private String resolveFallbackName(FirebaseUser user) {
        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }

        String email = user.getEmail();
        if (email != null) {
            int separator = email.indexOf('@');
            if (separator > 0) {
                return email.substring(0, separator);
            }
            if (!email.trim().isEmpty()) {
                return email;
            }
        }

        return "Người yêu phim";
    }
}
