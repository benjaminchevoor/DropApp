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

    private static class AccelerometerDropModel {

        enum State {
            /**
             * Actively monitoring sensor for drop.
             */
            MONITORING,

            /**
             * Period of time to wait for phone to settle (stop bouncing)
             * before transition to resting period.
             */
            DROP_GRACE_PERIOD,

            /**
             * Period of time to monitor if phone is moving. If no movement is detected at the
             * end of the {@link com.dropapp.services.AccelerometerService.AccelerometerDropModel#DROP_GRACE_PERIOD}
             * then this {@link DropListener} will be notified.
             */
            DROP_REST
        }

        private static final double DROPPED_VECTOR_THRESHOLD = 90.0;
        private static final double DROP_GRACE_PERIOD = 1_000;
        private static final double DROP_REST_PERIOD = 5_000;

        private final DropListener dropListener;
        private State currentState = State.MONITORING;
        private long dropEpoch;

        public AccelerometerDropModel(DropListener dropListener) {
            this.dropListener = dropListener;
        }

        private double getVector(float x, float y, float z) {
            return Math.sqrt((x * x) + (y * y) + (z * z));
        }

        public void newData(float x, float y, float z) {
            double vector = getVector(x, y, z);

            switch (this.dropListener.getState()) {
                case MONITORING:

                    switch (this.currentState) {
                        case MONITORING:
                            if (vector > DROPPED_VECTOR_THRESHOLD) {
                                this.dropEpoch = System.currentTimeMillis();
                                this.currentState = State.DROP_GRACE_PERIOD;
                            }
                            break;

                        case DROP_GRACE_PERIOD:
                            if (this.dropEpoch + DROP_GRACE_PERIOD < System.currentTimeMillis()) {
                                this.currentState = State.DROP_REST;
                            }
                            break;

                        case DROP_REST:
                            if (!validateRest(vector)) {
                                //user has moved their phone, reset
                                this.reset();
                            } else if (this.dropEpoch + DROP_GRACE_PERIOD + DROP_REST_PERIOD < System.currentTimeMillis()) {
                                this.dropListener.notifyDrop();
                            }
                            break;

                    }
                    break;

                case DROPPED_ALARMED:
                case DROPPED_NOTIFIED:
                    this.reset();
                    break;

            }
        }

        /**
         * Resets this model back to the original {@link State#MONITORING} state.
         */
        private void reset() {
            this.currentState = State.MONITORING;
            this.dropEpoch = 0;
        }

        /**
         * Checks to see if the provided vector is considered a resting value. Returns true if it
         * is, false otherwise.
         *
         * @param vector    the vector value
         * @return          true if resting, false otherwise.
         */
        private boolean validateRest(double vector) {
            return vector < 10.5 && vector > 9.0;
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
    private AccelerometerDropModel dropModel;

    public void initialize(DropListener dropListener, Context context) throws NoAccelerometerSensorException  {
        this.dropModel = new AccelerometerDropModel(dropListener);
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
