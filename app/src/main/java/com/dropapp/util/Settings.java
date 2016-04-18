package com.dropapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Soarnsky on 4/10/2016.
 */
public class Settings {

    private static final String IS_INITIALIZED = "isInitialized";
    private static final String IS_EMAIL_NOTIFICATIONS_ENABLED = "notificationEmailsEnabled";


    public static boolean hasFinishedSetup(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(IS_INITIALIZED, false);
    }

    public static void setSetupFinished(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(IS_INITIALIZED, true).apply();
    }

    public static void setEmail(Context context, String email) {
        // do stuffs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public static void isEmailEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();

        /*email_notification is the key*/

        /*return prefs.getBoolean(email_notification);*/

        edit.apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString("email", "");
    }

    public static void setThreshold (Context context) {
        // shake device, grab maximum value
        double threshold = 0;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        putDouble(editor, "threshold", threshold);
        editor.apply();
    }

    public static double getThreshold (Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return getDouble(prefs, "threshold", 0);
    }

    private static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static boolean isEmailNotificationsEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_EMAIL_NOTIFICATIONS_ENABLED, true);
    }

    public static void setEnableEmailNotification(Context context, boolean isEnabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(IS_EMAIL_NOTIFICATIONS_ENABLED, isEnabled).apply();
    }
}
