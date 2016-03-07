package com.dropapp.dropapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView outputTextView;
    private final AccelerometerService accelerometerService = new AccelerometerService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.outputTextView = (TextView) this.findViewById(R.id.outputTextView);

        this.accelerometerService.setRawAccelerometerDataListen(new AccelerometerService.RawAccelerometerDataListener() {
            @Override
            public void newData(float x, float y, float z) {
                outputTextView.append(String.format("%7.5f %7.5f %7.5f\n", x, y, z));
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.accelerometerService.uninitialize(this);
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
