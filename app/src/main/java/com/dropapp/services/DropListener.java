package com.dropapp.services;

/**
 * Created by benjaminchevoor on 3/28/16.
 */
public interface DropListener {

    enum State {
        MONITORING,
        DROPPED_NOTIFIED,
        DROPPED_ALARMED
    }

    /**
     * Called when the drop has been 'cleared' by a sensor.
     */
    void clearDrop();

    /**
     * Notifies the system that there has been a drop.
     */
    void notifyDrop();

    /**
     * Returns the current state of the drop service.
     * @return  the current {@link com.dropapp.services.DropListener.State}
     */
    State getState();
}
