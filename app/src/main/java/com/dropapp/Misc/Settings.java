package com.dropapp.Misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Soarnsky on 4/10/2016.
 */
public class Settings {

    public static void setEmail(Context context, String email) {
        // do stuffs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.commit();
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
        editor.commit();
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
}
