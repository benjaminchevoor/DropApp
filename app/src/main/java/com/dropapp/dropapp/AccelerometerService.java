package com.dropapp.dropapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by benjaminchevoor on 3/7/16.
 */
public class AccelerometerService implements SensorEventListener {

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

    public void initialize(Context context) throws NoAccelerometerSensorException  {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (this.accelerometer == null) {
            throw new NoAccelerometerSensorException();
        } else {
            sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
            if (this.listener != null) {
                this.listener.newData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }

    public void setRawAccelerometerDataListen(RawAccelerometerDataListener listener) {
        this.listener = listener;
    }
}
