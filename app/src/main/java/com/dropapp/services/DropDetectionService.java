package com.dropapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.dropapp.notifications.NotificationHandler;

public class DropDetectionService extends Service implements DropListener {

    private Context context;
    private final AccelerometerService accelerometerService = new AccelerometerService();
    private final DropServiceBinder dropDetectionService = new DropServiceBinder() {

        @Override
        public void clearDropNotification() {
            //todo clear notification
        }
    };

    @Override
    public void raiseDroppedNotification() {
        NotificationHandler.postNotification(this.context);
    }

    @Override
    public void raiseDroppedAlarm() {
        NotificationHandler.clearNotification(this.context);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this.getApplicationContext();

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
