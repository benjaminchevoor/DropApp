package com.dropapp.notifications;

import android.content.Context;

/**
 * Created by BarryB on 4/4/2016.
 */
public class UserNotifier {

    /**
     * Called when the device has detected a drop and the user should be notified, allowing them
     * to clear before the alarm.
     *
     * @param context   the application context
     */
    public static void raiseNotification(Context context) {
        //todo
    }

    /**
     * Called when the device has detected a drop and the user has not cleared the notification.
     *
     * @param context   the application context
     */
    public static void raiseAlarm(Context context) {
        //todo
    }

    /**
     * Called when the device has detected a drop and the alarm has not be cleared. The phone should
     * alert the user.
     *
     * @param context   the application context
     */
    public static void notifyPhoneLostEvent(Context context) {
        //todo
    }
}
