package com.dropapp.services;

import android.os.Binder;

/**
 * Created by benjaminchevoor on 3/28/16.
 */
public class DropServiceBinder extends Binder implements AccelerometerService.RawAccelerometerDataListener {

    private AccelerometerService.RawAccelerometerDataListener rawAccelerometerDataListener;

    public void setAccelerometerDataListener(AccelerometerService.RawAccelerometerDataListener listener) {
        this.rawAccelerometerDataListener = listener;
    }

    @Override
    public void newData(float x, float y, float z) {
        if (this.rawAccelerometerDataListener != null) {
            this.rawAccelerometerDataListener.newData(x, y, z);
        }
    }
}
