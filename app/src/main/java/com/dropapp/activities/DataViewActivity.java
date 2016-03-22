package com.dropapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.dropapp.R;
import com.dropapp.services.AccelerometerService;

public class DataViewActivity extends AppCompatActivity {

    private TextView outputTextView;
    private Bar xBar;
    private Bar yBar;
    private Bar zBar;
    private final AccelerometerService accelerometerService = new AccelerometerService();
    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        this.outputTextView = (TextView) this.findViewById(R.id.outputTextView);

        final BarChartView barChartView = (BarChartView) this.findViewById(R.id.barChartView);
        barChartView.setBarBackgroundColor(Color.BLACK);
        barChartView.setBarSpacing(10);
        barChartView.setSetSpacing(10);
        barChartView.setLabelsColor(Color.WHITE);
        barChartView.setAxisColor(Color.GRAY);
        barChartView.setAxisBorderValues(-20, 20, 2);

        this.xBar = new Bar("x", 0.0f);
        this.yBar = new Bar("y", 0.0f);
        this.zBar = new Bar("z", 0.0f);
        this.xBar.setColor(Color.WHITE);
        this.yBar.setColor(Color.WHITE);
        this.zBar.setColor(Color.WHITE);

        BarSet barSet = new BarSet();
        barSet.addBar(xBar);
        barSet.addBar(yBar);
        barSet.addBar(zBar);
        barChartView.addData(barSet);
        barChartView.show();

        this.accelerometerService.setRawAccelerometerDataListen(new AccelerometerService.RawAccelerometerDataListener() {
            @Override
            public void newData(float x, float y, float z) {
                outputTextView.append(String.format("x:%9.5f y:%9.5f z:%9.5f\n", x, y, z));

                xBar.setValue(x);
                yBar.setValue(y);
                zBar.setValue(z);
                barChartView.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            this.accelerometerService.initialize(this);
            this.outputTextView.setText("Accelerometer found. Starting...\n");
        } catch (AccelerometerService.NoAccelerometerSensorException e) {
            this.outputTextView.setText("Error! No accelerometer available.");
        }

        this.isResumed = true;

        //forces the scroll view to the bottom
        final ScrollView scrollView = (ScrollView) this.findViewById(R.id.scrollView);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isResumed) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.accelerometerService.uninitialize(this);

        this.isResumed = false;
    }

}
