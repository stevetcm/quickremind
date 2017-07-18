package com.orangemuffin.quickremind.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.activities.MainActivity;
import com.orangemuffin.quickremind.models.Reminder;
import com.orangemuffin.quickremind.receivers.DismissReceiver;

/* Created by OrangeMuffin on 7/7/2017 */
public class NotificationUtil {

    public static void createNotification(Context context, Reminder reminder) {
        //retrieve reminder content
        String mContent = reminder.getContent();

        //variable to hold intent to MainActivity
        Intent mainIntent = new Intent(context, MainActivity.class);

        //composing notification skeleton
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_status_bar_white)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(mContent)
                .setTicker(mContent);

        /*//setup to deal with sound option settings
        String soundUri = "content://settings/system/notification_sound";
        mBuilder.setSound(Uri.parse(soundUri));*/

        //setup to deal with LED light option enable
        mBuilder.setLights(Color.BLUE, 700, 1500);

        //setup to deal with vibrate option enable
        long[] pattern = {0, 300, 0};
        mBuilder.setVibrate(pattern);

        //setup to deal with persistent reminders
        if (reminder.getPersistent().equals("true")) {
            mBuilder.setOngoing(true);
            Intent dismissIntent = new Intent(context, DismissReceiver.class);
            dismissIntent.putExtra("REMINDER_ID", Integer.toString(reminder.getId()));
            PendingIntent dismissPending = PendingIntent.getBroadcast(context, reminder.getId(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(0, "Mark as Done", dismissPending);
        }

        //setup to deal with repeating reminders
        if (reminder.getRepeat().equals("false")) {
            mainIntent.putExtra("switchPage", true); //switch page to ended
        }

        //set up how to deal with on click operation
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        //build and trigger notification
        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(reminder.getId(), mBuilder.build());
    }

    public static void cancelNotification(Context context, int reminderID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(reminderID);
    }
}
