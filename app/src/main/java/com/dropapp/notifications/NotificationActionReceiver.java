package com.dropapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dropapp.services.UserActionListener;

/**
 * Created by BarryB on 4/16/2016.
 */
public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String ACTION_CLEAR_DROP = "com.dropapp.cleardrop";
    public static final String EXTRA_NOTIFICATION = "notification";
    public static final String EXTRA_ALARM = "alarm";

    public static final int REQUEST_CODE = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_NOTIFICATION)) {
                UserActionListener.clearNotification();
            } else if (intent.hasExtra(EXTRA_ALARM)) {
                UserActionListener.clearAlarm();
            }

            UserNotifier.clearNotification(context);
        }
    }
}
