package com.example.movie_ticket_app.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.movie_ticket_app.R;

public final class BookingNotificationHelper {
    private static final String CHANNEL_ID = "booking_status_channel";

    private BookingNotificationHelper() {
    }

    public static void showSuccess(Context context, String body) {
        if (!canPostNotifications(context)) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Booking Updates",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Booking confirmed")
            .setContentText("Booking confirmed")
            .setStyle(new NotificationCompat.BigTextStyle().bigText("Booking confirmed"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private static boolean canPostNotifications(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }

        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }
}