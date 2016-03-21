package com.dropapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DropDetectionService extends Service {

    private final AccelerometerService accelerometerService = new AccelerometerService();

    public static void dropDetected() {
        //TODO
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.accelerometerService.initialize(this.getApplicationContext());
        } catch (AccelerometerService.NoAccelerometerSensorException e) {
            //BAD
            e.printStackTrace();
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //todo
        return null;
    }

    @Override
    public void onDestroy() {
        this.accelerometerService.uninitialize(this.getApplicationContext());
    }
}
