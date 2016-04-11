package com.dropapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.dropapp.logger.DropStateLogger;
import com.dropapp.logger.Logger;
import com.dropapp.notifications.UserNotifier;

import java.util.Timer;
import java.util.TimerTask;

public class DropDetectionService extends Service {

    private class UserActionListenerImpl implements UserActionListener.Provider {

        @Override
        public void clearNotification() {
            dropListener.clearDrop();
        }

        @Override
        public void clearAlarm() {
            dropListener.clearDrop();
        }
    }

    private class DropListenerImpl implements DropListener {

        private static final long DROPPED_NOTIFY_PERIOD_MILLIS = 5_000;
        private static final long DROPPED_ALARM_PERIOD_MILLIS = 25_000;

        private final Timer timer = new Timer();
        private TimerTask task;
        private State currentState = State.MONITORING;

        @Override
        public synchronized void clearDrop() {
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
                this.currentState = State.MONITORING;
            }
        }

        @Override
        public void notifyDrop() {
            doTransition();
        }

        private synchronized void doTransition() {
            switch (this.getState()) {
                case MONITORING:
                    this.currentState = State.DROPPED_NOTIFIED;
                    this.scheduleTask(DROPPED_NOTIFY_PERIOD_MILLIS);
                    UserNotifier.raiseNotification(this.getContext());

                    try {
                        DropStateLogger.logTransition(this.getContext(), State.DROPPED_NOTIFIED);
                    } catch (Exception e) {
                        //do nothing
                    }
                    break;

                case DROPPED_NOTIFIED:
                    this.currentState = State.DROPPED_ALARMED;
                    this.scheduleTask(DROPPED_ALARM_PERIOD_MILLIS);
                    UserNotifier.raiseAlarm(this.getContext());

                    try {
                        DropStateLogger.logTransition(this.getContext(), State.DROPPED_ALARMED);
                    } catch (Exception e) {
                        //do nothing
                    }
                    break;

                case DROPPED_ALARMED:
                    this.currentState = State.MONITORING;
                    UserNotifier.notifyPhoneLostEvent(this.getContext());

                    try {
                        DropStateLogger.logTransition(this.getContext(), State.MONITORING);
                    } catch (Exception e) {
                        //do nothing
                    }
                    break;
            }
        }

        private Context getContext() {
            return DropDetectionService.this.context;
        }

        private void scheduleTask(long delayMillis) {
            if (this.task != null) {
                this.task.cancel();
            }

            this.task = new TimerTask() {
                @Override
                public void run() {
                    doTransition();
                }
            };

            this.timer.schedule(task, delayMillis);
        }

        @Override
        public State getState() {
            return this.currentState;
        }
    }

    public static boolean IS_RUNNING = false;

    private Context context;
    private final AccelerometerService accelerometerService = new AccelerometerService();
    private final DropServiceBinder dropDetectionService = new DropServiceBinder();
    private final DropListenerImpl dropListener = new DropListenerImpl();
    private final UserActionListenerImpl userActionListener = new UserActionListenerImpl();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this.getApplicationContext();

        try {
            this.accelerometerService.initialize(this.dropListener, this.getApplicationContext());
            this.accelerometerService.setRawAccelerometerDataListener(this.dropDetectionService);
        } catch (AccelerometerService.NoAccelerometerSensorException e) {
            //BAD
            e.printStackTrace();
        }

        UserActionListener.registerProvider(this.userActionListener);

        IS_RUNNING = true;

        Toast.makeText(this.context, "Started!", Toast.LENGTH_SHORT).show();

        try {
            Logger.start();
        } catch (Exception e) {
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
        UserActionListener.unregisterProvider(this.userActionListener);

        Toast.makeText(this.context, "Stopped!", Toast.LENGTH_SHORT).show();

        IS_RUNNING = false;

        Logger.stop();
    }
}
