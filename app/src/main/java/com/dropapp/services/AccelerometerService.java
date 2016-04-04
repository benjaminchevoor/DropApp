package com.dropapp.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by benjaminchevoor on 3/7/16.
 */
public class AccelerometerService implements SensorEventListener {

    private class AccelerometerDropModel {

        public void newData(float x, float y, float z) {
            //TODO write drop detection algorithm here

            boolean dropDetected = false;

            if (dropDetected) {
                AccelerometerService.this.dropListener.raiseDroppedNotification();
            }
        }

    }

    /**
     * An Exception raised when no accelerometer sensor is available on this device.
     */
    public class NoAccelerometerSensorException extends Exception {

    }

    public interface RawAccelerometerDataListener {
        void newData(float x, float y, float z);
    }

    private Sensor accelerometer;
    private RawAccelerometerDataListener listener;
    private final AccelerometerDropModel dropModel = new AccelerometerDropModel();
    private DropListener dropListener;

    public void initialize(DropListener dropListener, Context context) throws NoAccelerometerSensorException  {
        this.dropListener = dropListener;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (this.accelerometer == null) {
            throw new NoAccelerometerSensorException();
        } else {
            sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void uninitialize(Context context) {
        if (this.accelerometer != null) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this, this.accelerometer);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            this.dropModel.newData(x, y, z);

            if (this.listener != null) {
                this.listener.newData(x, y, z);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }

    public void setRawAccelerometerDataListener(RawAccelerometerDataListener listener) {
        this.listener = listener;
    }
}
