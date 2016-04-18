package com.dropapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean isCalibrating = false;
    private int runningAttempts = 0;
    private int runningAverage = 0;

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
                runningAverage = 0;
                runningAttempts = 0;
                startCalibrationAttempt();
            }
        });

        return parentView;
    }

    private void startCalibrationAttempt() {
        if (!isCalibrating) {
            this.isCalibrating = true;
            this.runningAttempts++;

            this.thresholdTextView.setText("");

            final Toast t = Toast.makeText(getContext(), "...", Toast.LENGTH_SHORT);

            final Handler h = new Handler();
            Runnable toastRunnable = new Runnable() {
                private int i = 3;

                @Override
                public void run() {
                    if (i > 0) {
                        t.setText(i-- + "...");
                        t.show();
                        h.postDelayed(this, 1000);
                    } else {
                        t.setText("Drop!");
                        t.show();
                    }
                }
            };

            h.post(toastRunnable);

            Runnable calibrationRunnable = new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    new Calibration(getContext());
                }
            };
            h.postDelayed(calibrationRunnable, 3000);
        }
    }

    private void stopCalibrationAttempt(double newThreshold) {
        this.isCalibrating = false;
        int threshold = (int) Math.round(newThreshold);
        this.runningAverage += threshold;

        Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        Toast.makeText(getContext(), String.format("Threshold: %d", threshold), Toast.LENGTH_SHORT).show();

        if (runningAttempts < 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Drop again!");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    startCalibrationAttempt();
                }
            });
            builder.setPositiveButton("Okay!", null);
            builder.create().show();

        } else {
            int average = this.runningAverage / 3;

            this.thresholdTextView.setText(String.format("New threshold: %d", average));
            this.progressBar.setVisibility(View.INVISIBLE);

            Settings.setThreshold(this.getContext(), newThreshold);

            ((SetupActivity) this.getActivity()).calibrationFinished();
        }
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

                    stopCalibrationAttempt(this.highestVector);
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
