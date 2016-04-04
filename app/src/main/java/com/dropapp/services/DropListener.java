package com.dropapp.services;

/**
 * Created by benjaminchevoor on 3/28/16.
 */
public interface DropListener {

    /**
     * Called when a drop has been detected by a sensor.
     */
    void raiseDroppedNotification();

    /**
     * Called when a drop has been detected and the notification has not been cleared.
     */
    void raiseDroppedAlarm();

}
