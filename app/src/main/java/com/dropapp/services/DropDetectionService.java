package com.dropapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DropDetectionService extends Service implements DropListener {

    private final AccelerometerService accelerometerService = new AccelerometerService();
    private final DropServiceBinder dropDetectionService = new DropServiceBinder() {

        @Override
        public void clearDropNotification() {
            //todo
        }
    };

    @Override
    public void dropDetected() {
        //TODO handle notification
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.accelerometerService.initialize(this, this.getApplicationContext());
            this.accelerometerService.setRawAccelerometerDataListener(this.dropDetectionService);
        } catch (AccelerometerService.NoAccelerometerSensorException e) {
            //BAD
            e.printStackTrace();
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.dropDetectionService;
    }

    @Override
    public void onDestroy() {
        this.accelerometerService.uninitialize(this.getApplicationContext());
    }
}
