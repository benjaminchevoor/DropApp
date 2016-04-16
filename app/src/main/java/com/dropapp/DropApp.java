package com.dropapp;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by BarryB on 4/16/2016.
 */
public class DropApp extends Application {

    private Tracker tracker;

    public synchronized Tracker getDefaultTracker() {
        if (this.tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            this.tracker = analytics.newTracker(R.xml.dropapp_tracker);
        }

        return this.tracker;
    }

}
