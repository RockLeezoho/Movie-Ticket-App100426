package com.example.movie_ticket_app.data;

import com.example.movie_ticket_app.models.Movie;
import com.example.movie_ticket_app.models.Payment;
import com.example.movie_ticket_app.models.Showtime;
import com.example.movie_ticket_app.models.Theater;
import com.example.movie_ticket_app.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SampleDataSeeder {
    private static final long SEED_VERSION = 2L;

    private SampleDataSeeder() {
    }

    public static void seedIfNeeded(Runnable onComplete) {
        DatabaseReference rootRef = FirebaseDb.getRootReference();
        Map<String, Object> updates = new HashMap<>();
        seedMovies(updates);
        seedTheaters(updates);
        seedShowtimes(updates);
        updates.put("seedVersion", SEED_VERSION);
        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    private static void seedMovies(Map<String, Object> updates) {
        Movie avatar = new Movie("m1", "Avatar: The Way of Water",
                "Jake Sully and his family face a powerful threat and fight to protect Pandora.",
                "https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                "Sci-Fi", 7.8, 192);

        Movie wick = new Movie("m2", "John Wick: Chapter 4",
                "John Wick uncovers a path to defeating The High Table.",
                "https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg",
                "Action", 8.1, 169);

        Movie spider = new Movie("m3", "Spider-Man: Across the Spider-Verse",
                "Miles Morales catapults across the Multiverse and meets new Spider allies.",
                "https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg",
                "Animation", 8.6, 140);

        updates.put(FirebasePaths.MOVIES + "/" + avatar.getId(), avatar);
        updates.put(FirebasePaths.MOVIES + "/" + wick.getId(), wick);
        updates.put(FirebasePaths.MOVIES + "/" + spider.getId(), spider);
    }

    private static void seedTheaters(Map<String, Object> updates) {
        Theater t1 = new Theater("t1", "Cineplex Central", "123 Movie St, Cinema City");
        Theater t2 = new Theater("t2", "Starlight Cinema", "456 Star Blvd, Galaxy Town");
        Theater t3 = new Theater("t3", "Grand Theater", "789 Main Ave, Downtown");

        updates.put(FirebasePaths.THEATERS + "/" + t1.getId(), t1);
        updates.put(FirebasePaths.THEATERS + "/" + t2.getId(), t2);
        updates.put(FirebasePaths.THEATERS + "/" + t3.getId(), t3);
    }

    private static void seedShowtimes(Map<String, Object> updates) {
        addShowtime(updates, "s1", "m1", "Avatar: The Way of Water", "t1", "Cineplex Central", 10, 0, 12.5);
        addShowtime(updates, "s2", "m1", "Avatar: The Way of Water", "t2", "Starlight Cinema", 19, 30, 15.0);
        addShowtime(updates, "s3", "m2", "John Wick: Chapter 4", "t1", "Cineplex Central", 16, 0, 13.0);
        addShowtime(updates, "s4", "m2", "John Wick: Chapter 4", "t3", "Grand Theater", 21, 0, 15.5);
        addShowtime(updates, "s5", "m3", "Spider-Man: Across the Spider-Verse", "t2", "Starlight Cinema", 14, 15, 11.0);
        addShowtime(updates, "s6", "m3", "Spider-Man: Across the Spider-Verse", "t3", "Grand Theater", 18, 45, 12.0);
    }

    private static void addShowtime(Map<String, Object> updates, String id, String movieId, String movieTitle,
                                    String theaterId, String theaterName, int hour, int minute, double price) {
        long startAt = millisTodayOrTomorrow(hour, minute);
        String displayTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        Showtime showtime = new Showtime(id, movieId, theaterId, displayTime, price);
        showtime.setMovieTitle(movieTitle);
        showtime.setTheaterName(theaterName);
        showtime.setStartTimeMillis(startAt);
        showtime.setBookedSeats(sampleBookedSeatsForShowtime(id));

        updates.put(FirebasePaths.SHOWTIMES + "/" + id, showtime);
    }

    private static java.util.List<String> sampleBookedSeatsForShowtime(String showtimeId) {
        switch (showtimeId) {
            case "s1":
                return Arrays.asList("A1", "A2", "B7");
            case "s2":
                return Arrays.asList("C3", "C4", "D10");
            case "s3":
                return Arrays.asList("A5", "B1");
            case "s4":
                return Arrays.asList("D6", "E8", "E9");
            case "s5":
                return Arrays.asList("A3", "A4", "C9");
            case "s6":
                return Arrays.asList("B2", "B3", "D12");
            default:
                return new java.util.ArrayList<>();
        }
    }

    private static long millisTodayOrTomorrow(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar scheduled = Calendar.getInstance();
        scheduled.set(Calendar.HOUR_OF_DAY, hour);
        scheduled.set(Calendar.MINUTE, minute);
        scheduled.set(Calendar.SECOND, 0);
        scheduled.set(Calendar.MILLISECOND, 0);

        if (scheduled.before(now)) {
            scheduled.add(Calendar.DAY_OF_YEAR, 1);
        }
        return scheduled.getTimeInMillis();
    }

    public static void seedDemoUserBookingsIfNeeded() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDb.getRootReference();
        rootRef.child(FirebasePaths.TICKETS).orderByChild("userId").equalTo(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        return;
                    }

                    Map<String, Object> updates = new HashMap<>();
                    seedDemoBookings(userId, updates);
                    rootRef.updateChildren(updates);
                });
    }

    private static void seedDemoBookings(String userId, Map<String, Object> updates) {
        long completedShowtimeMillis = futureMillisHoursFromNow(3);
        long failedShowtimeMillis = futureMillisHoursFromNow(8);

        Ticket completedTicket = new Ticket(
                "demo-ticket-1",
                userId,
                "s1",
                "demo-payment-1",
                "Avatar: The Way of Water",
                "Cineplex Central",
                "10:00",
                "B7",
                12.5,
                completedShowtimeMillis
        );

        Ticket anotherTicket = new Ticket(
                "demo-ticket-2",
                userId,
                "s5",
                "demo-payment-2",
                "Spider-Man: Across the Spider-Verse",
                "Starlight Cinema",
                "14:15",
                "D12",
                11.0,
                failedShowtimeMillis
        );

        Payment completedPayment = new Payment(
                "demo-payment-1",
                userId,
                "demo-ticket-1",
                "s1",
                "Avatar: The Way of Water",
                "Cineplex Central",
                "B7",
                "Sandbox Wallet",
                "COMPLETED",
                12.5,
                System.currentTimeMillis() - 86_400_000L
        );

        Payment failedPayment = new Payment(
                "demo-payment-2",
                userId,
                null,
                "s5",
                "Spider-Man: Across the Spider-Verse",
                "Starlight Cinema",
                "D12",
                "Sandbox Card",
                "FAILED",
                11.0,
                System.currentTimeMillis() - 43_200_000L
        );

        updates.put(FirebasePaths.TICKETS + "/" + completedTicket.getId(), completedTicket);
        updates.put(FirebasePaths.TICKETS + "/" + anotherTicket.getId(), anotherTicket);
        updates.put(FirebasePaths.PAYMENTS + "/" + completedPayment.getId(), completedPayment);
        updates.put(FirebasePaths.PAYMENTS + "/" + failedPayment.getId(), failedPayment);
    }

    private static long futureMillisHoursFromNow(int hoursAhead) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hoursAhead);
        return calendar.getTimeInMillis();
    }
}


