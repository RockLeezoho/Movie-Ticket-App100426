package com.example.movie_ticket_app.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseDb {
    private static final String DATABASE_URL = "https://movie-ticket-app-83be4-default-rtdb.firebaseio.com";
    private static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance(DATABASE_URL);

    private FirebaseDb() {
    }

    public static FirebaseDatabase getDatabase() {
        return DATABASE;
    }

    public static String getDatabaseUrl() {
        return DATABASE_URL;
    }

    public static DatabaseReference getRootReference() {
        return DATABASE.getReference();
    }
}