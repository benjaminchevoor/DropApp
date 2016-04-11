package com.dropapp.services;

/**
 * Created by BarryB on 4/4/2016.
 */
public class UserActionListener {

    interface Provider {
        void clearNotification();
        void clearAlarm();
    }

    private static Provider PROVIDER;

    /**
     * Called when the user clears the drop notification.
     */
    public static void clearNotification() {
        Provider p = PROVIDER;

        if (p != null) {
            p.clearNotification();
        }
    }

    /**
     * Called when the user clears the alarm notification.
     */
    public static void clearAlarm() {
        Provider p = PROVIDER;

        if (p != null) {
            p.clearAlarm();
        }
    }

    static void registerProvider(Provider provider) {
        PROVIDER = provider;
    }

    static void unregisterProvider(Provider provider) {
        if (provider == PROVIDER) {
            PROVIDER = null;
        }
    }

}
