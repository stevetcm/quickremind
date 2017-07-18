package com.orangemuffin.quickremind.receivers;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.activities.MainActivity;
import com.orangemuffin.quickremind.database.ReminderDatabase;
import com.orangemuffin.quickremind.models.Reminder;
import com.orangemuffin.quickremind.utils.NotificationUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/* Created by OrangeMuffin on 6/28/2017 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderID = Integer.parseInt(intent.getStringExtra("REMINDER_ID"));

        //retrieve reminder from database
        ReminderDatabase rb = new ReminderDatabase(context);
        Reminder reminder = rb.getReminder(reminderID);

        //build up notification
        NotificationUtil.createNotification(context, reminder);

        //setup next alarm if repeating or end current reminder
        if (reminder.getRepeat().equals("true")) {
            setNextAlarm(context, reminder, rb);
        } else {
            reminder.setActive("false");
            rb.updateReminder(reminder);
        }

        //broadcast refresh intent to update display fragments
        Intent updateIntent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
    }

    @TargetApi(23)
    public void setAlarm(Context context, Calendar calendar, int ID) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //create pending intent with reminder ID to be executed
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("REMINDER_ID", Integer.toString(ID));
        pendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //set alarm using reminder calendar time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    //Since VERSION_CODES.KITKAT(SDK19), repeating alarms are inexact and have to be set manually
    public void setNextAlarm(Context context, Reminder reminder, ReminderDatabase rb) {
       //setting up current calendar for reminder to be modified
        Calendar calendar = Calendar.getInstance();
        String dateandtime = reminder.getDate() + " " + reminder.getTime();
        try {
            SimpleDateFormat dt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
            Date converted = dt.parse(dateandtime);
            dt = new SimpleDateFormat("yyyyMMddHHmm");
            dateandtime = dt.format(converted);
            calendar.setTime(dt.parse(dateandtime));
        } catch (Exception e) { }

        //setting up next timer according to the repeating duration
        if (reminder.getRepeatType().equals("Minute(s)")) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(reminder.getRepeatNo()));
        } else if (reminder.getRepeatType().equals("Hour(s)")) {
            calendar.add(Calendar.HOUR, Integer.parseInt(reminder.getRepeatNo()));
        } else if (reminder.getRepeatType().equals("Day(s)")) {
            calendar.add(Calendar.DATE, Integer.parseInt(reminder.getRepeatNo()));
        } else if (reminder.getRepeatType().equals("Week(s)")) {
            calendar.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(reminder.getRepeatNo()));
        } else if (reminder.getRepeatType().equals("Month(s)")) {
            calendar.add(Calendar.MONTH, Integer.parseInt(reminder.getRepeatNo()));
        }

        //re-setup date configuration
        String pattern_date = "EEE, dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern_date);
        reminder.setDate(dateFormat.format(calendar.getTime()));

        //re-setup time configuration
        String pattern_time = "HH:mm";
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern_time);
        reminder.setTime(dateFormat2.format(calendar.getTime()));

        //update current reminder in the database
        rb.updateReminder(reminder);

        //set next alarm with updated timer
        setAlarm(context, calendar, reminder.getId());
    }

    public void cancelAlarm(Context context, int ID) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //cancel alarm using reminder ID
        Intent intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, ID, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void cancelRepeating(Context context, Reminder reminder, ReminderDatabase rb) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //cancel alarm using reminder ID
        Intent intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, 0);
        alarmManager.cancel(pendingIntent);

        //setting up current calendar for reminder to be modified
        Calendar calendar = Calendar.getInstance();
        String dateandtime = reminder.getDate() + " " + reminder.getTime();
        try {
            SimpleDateFormat dt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
            Date converted = dt.parse(dateandtime);
            dt = new SimpleDateFormat("yyyyMMddHHmm");
            dateandtime = dt.format(converted);
            calendar.setTime(dt.parse(dateandtime));
        } catch (Exception e) { }

        //rewind calendar to last reminder alarm timer
        if (reminder.getRepeatType().equals("Minute(s)")) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(reminder.getRepeatNo()) * -1);
        } else if (reminder.getRepeatType().equals("Hour(s)")) {
            calendar.add(Calendar.HOUR, Integer.parseInt(reminder.getRepeatNo()) * -1);
        } else if (reminder.getRepeatType().equals("Day(s)")) {
            calendar.add(Calendar.DATE, Integer.parseInt(reminder.getRepeatNo()) * -1);
        } else if (reminder.getRepeatType().equals("Week(s)")) {
            calendar.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(reminder.getRepeatNo()) * -1);
        } else if (reminder.getRepeatType().equals("Month(s)")) {
            calendar.add(Calendar.MONTH, Integer.parseInt(reminder.getRepeatNo()) * -1);
        }

        //re-setup date configuration
        String pattern_date = "EEE, dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern_date);
        reminder.setDate(dateFormat.format(calendar.getTime()));

        //re-setup time configuration
        String pattern_time = "HH:mm";
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern_time);
        reminder.setTime(dateFormat2.format(calendar.getTime()));

        //disable reminder entry
        reminder.setActive("false");

        //update current reminder in the database
        rb.updateReminder(reminder);

        //broadcast refresh intent to update display fragments
        Intent updateIntent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
    }
}
