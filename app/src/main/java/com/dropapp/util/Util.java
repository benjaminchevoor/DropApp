package com.dropapp.util;

import android.app.Activity;

import com.dropapp.DropApp;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by BarryB on 4/16/2016.
 */
public class Util {

    public static Tracker getTracker(Activity activity) {
        DropApp dropApp = (DropApp) activity.getApplication();
        return dropApp.getDefaultTracker();
    }
}
