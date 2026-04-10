package com.example.movie_ticket_app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.movie_ticket_app.models.Ticket;
import com.example.movie_ticket_app.receivers.ReminderBroadcastReceiver;

public final class ReminderScheduler {
    private static final long REMINDER_OFFSET_MILLIS = 60 * 60 * 1000L;

    private ReminderScheduler() {
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
        intent.putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, "Showtime in 1 hour");
        intent.putExtra(ReminderBroadcastReceiver.EXTRA_MESSAGE,
                ticket.getMovieTitle() + " at " + ticket.getTheaterName() + " starts soon.");

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
}


