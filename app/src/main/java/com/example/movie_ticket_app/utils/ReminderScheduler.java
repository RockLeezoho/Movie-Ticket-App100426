package com.example.movie_ticket_app.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.models.Ticket;
import com.example.movie_ticket_app.receivers.ReminderBroadcastReceiver;

public final class ReminderScheduler {
    private static final long REMINDER_OFFSET_MILLIS = 60 * 60 * 1000L;
    private static final String CHANNEL_ID = "movie_reminders";

    private ReminderScheduler() {
    }

    public static void showNow(Context context, Ticket ticket) {
        if (!canPostNotifications(context)) {
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nhắc lịch chiếu",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        String title = "Nhắc giờ chiếu phim";
        String message = "Phim " + ticket.getMovieTitle()
            + " sẽ chiếu lúc " + ticket.getTime()
            + " tại " + ticket.getTheaterName()
            + ". Ghế: " + ticket.getSeatNumber() + ".";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void schedule(Context context, Ticket ticket) {
        long showtimeMillis = ticket.getShowtimeMillis();
        if (showtimeMillis <= 0) {
            return;
        }

        long triggerAt = showtimeMillis - REMINDER_OFFSET_MILLIS;
        if (triggerAt <= System.currentTimeMillis()) {
            triggerAt = System.currentTimeMillis() + 10_000L;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, "Nhắc lịch chiếu");
        intent.putExtra(ReminderBroadcastReceiver.EXTRA_MESSAGE,
            "Phim " + ticket.getMovieTitle() + " tại " + ticket.getTheaterName() + " sẽ chiếu sau 1 giờ.");

        int requestCode = ticket.getId() == null ? (int) System.currentTimeMillis() : ticket.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }

    private static boolean canPostNotifications(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }

        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }
}


