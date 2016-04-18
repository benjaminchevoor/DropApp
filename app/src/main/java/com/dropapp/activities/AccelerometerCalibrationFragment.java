package com.dropapp.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dropapp.R;
import com.dropapp.util.Settings;


public class AccelerometerCalibrationFragment extends Fragment {


    public AccelerometerCalibrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccelerometerCalibrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccelerometerCalibrationFragment newInstance() {
        return new AccelerometerCalibrationFragment();
    }

    private TextView thresholdTextView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_accelerometer_calibration, container, false);

        this.progressBar = (ProgressBar) parentView.findViewById(R.id.progressBar);
        this.thresholdTextView = (TextView) parentView.findViewById(R.id.thresholdTextView);

        Button startCalibrationButton = (Button) parentView.findViewById(R.id.startCalibrationButton);
        startCalibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalibration();
            }
        });

        return parentView;
    }

    private void startCalibration() {
        this.thresholdTextView.setText("");
        this.progressBar.setVisibility(View.VISIBLE);

        new Calibration(this.getContext());
    }

    private void stopCalibration(double newThreshold) {
        this.thresholdTextView.setText(String.format("New threshold: %.4f", newThreshold));
        this.progressBar.setVisibility(View.INVISIBLE);

        Settings.setThreshold(this.getContext(), newThreshold);

        Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        ((SetupActivity) this.getActivity()).calibrationFinished();
    }

    private class Calibration implements SensorEventListener {

        private static final long RUN_TIME_MILLIS = 3_000;

        private final Context context;
        private final long startEpoch = System.currentTimeMillis();
        private double highestVector = 0.0;

        public Calibration(Context context) {
            this.context = context;

            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double vector = getVector(x, y, z);

                if (this.highestVector < vector) {
                    this.highestVector = vector;
                }

                if (this.startEpoch + RUN_TIME_MILLIS < System.currentTimeMillis()) {
                    SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                    sensorManager.unregisterListener(this);

                    stopCalibration(this.highestVector);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //do nothing
        }

        private double getVector(float x, float y, float z) {
            return Math.sqrt((x * x) + (y * y) + (z * z));
        }
    }
}
