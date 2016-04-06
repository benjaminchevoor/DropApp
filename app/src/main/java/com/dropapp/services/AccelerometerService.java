package com.dropapp.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dropapp.logger.AccelerometerLogger;

/**
 * Created by benjaminchevoor on 3/7/16.
 */
public class AccelerometerService implements SensorEventListener {

    public static class AccelerometerDropModel {

        public enum State {
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

        private final Context context;
        private final DropListener dropListener;
        private State currentState = State.MONITORING;
        private long dropEpoch;
        //prevous ten reads. 0 = last read, 9 = tenth past read;
        private double[] prevTen = new double[9];

        public AccelerometerDropModel(DropListener dropListener, Context context) {
            this.dropListener = dropListener;
            this.context = context;
            //initialize previous ten reads
            for (int i = 0; i <= 9; i++)
                this.prevTen[i] = -1;
        }

        private double getVector(float x, float y, float z) {
            return Math.sqrt((x * x) + (y * y) + (z * z));
        }

        private void storePrevTen(double curAvg){
            for(int i = 9; i >=1; i--){
                this.prevTen[i] = this.prevTen[i-1];
            }
            this.prevTen[0] = curAvg;
        }

        private double getAvgOfPrevTen(){
            double avg = -1;
            int count = 10;
            double sum = 0;
            for(int i = 0; i<=9; i++){
                if(this.prevTen[i] == -1){
                    count--;
                }
                else
                    sum = sum + this.prevTen[i];
            }
            avg = sum/count;

            return avg;
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

                                try {
                                    AccelerometerLogger.logTransition(this.context, State.DROP_GRACE_PERIOD);
                                } catch (Exception e) {
                                    //do nothing
                                }
                            }
                            break;

                        case DROP_GRACE_PERIOD:
                            if (this.dropEpoch + DROP_GRACE_PERIOD < System.currentTimeMillis()) {
                                this.currentState = State.DROP_REST;
                                try {
                                    AccelerometerLogger.logTransition(this.context, State.DROP_REST);
                                } catch (Exception e) {
                                    //do nothing
                                }
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
            if (this.currentState != State.MONITORING) {
                try {
                    AccelerometerLogger.logTransition(this.context, State.MONITORING);
                } catch (Exception e) {
                    //do nothing
                }
            }

            this.currentState = State.MONITORING;
            this.dropEpoch = 0;
            //reset previous ten reads
            for (int i = 0; i <= 9; i++)
                this.prevTen[i] = -1;
        }

        /**
         * Checks to see if the provided vector is considered a resting value. Returns true if it
         * is, false otherwise.
         *
         * @param vector    the vector value
         * @return          true if resting, false otherwise.
         */
        private boolean validateRest(double vector) {
            // Kind of dangerous because this assumes vector = rest avg so this all depends
            // on the amount of time we wait to validate to ensure vector = rest avg
            for(int i = 0; i<=9; i++){
                if(this.prevTen[i] > vector)
                    if(this.prevTen[i] - vector > 1.5)
                        return false;
                    else if(this.prevTen[i] - vector < -1.5)
                        return false;
            }
            return true;
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
    private Context context;

    public void initialize(DropListener dropListener, Context context) throws NoAccelerometerSensorException  {
        this.context = context;
        this.dropModel = new AccelerometerDropModel(dropListener, this.context);
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
