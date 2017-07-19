package com.orangemuffin.quickremind.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orangemuffin.quickremind.database.ReminderDatabase;
import com.orangemuffin.quickremind.models.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* Created by OrangeMuffin on 6/29/2017 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ReminderDatabase rb = new ReminderDatabase(context);
            AlarmReceiver mAlarmReceiver = new AlarmReceiver();

            Calendar mCalendar = Calendar.getInstance();
            List<Reminder> reminders = rb.getAllReminders();

            for (Reminder reminder : reminders) {
                if (reminder.getActive().equals("true")) {
                    String dateandtime = reminder.getDate() + " " + reminder.getTime();
                    try {
                        SimpleDateFormat dt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
                        Date converted = dt.parse(dateandtime);
                        dt = new SimpleDateFormat("yyyyMMddHHmm");
                        dateandtime = dt.format(converted);
                        mCalendar.setTime(dt.parse(dateandtime));
                    } catch (Exception e) { }

                    mAlarmReceiver.setAlarm(context, mCalendar, reminder.getId());
                }
            }
        }
    }
}
