package com.orangemuffin.quickremind.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orangemuffin.quickremind.utils.NotificationUtil;

/* Created by OrangeMuffin on 7/3/2017 */
public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderID = Integer.parseInt(intent.getStringExtra("REMINDER_ID"));
        NotificationUtil.cancelNotification(context, reminderID);
    }
}
