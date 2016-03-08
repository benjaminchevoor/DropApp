package com.dropapp.dropapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;

public class MainActivity extends Activity {

    private TextView outputTextView;
    private Bar xBar;
    private Bar yBar;
    private Bar zBar;
    private final AccelerometerService accelerometerService = new AccelerometerService();
    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}