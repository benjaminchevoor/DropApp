package com.dropapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dropapp.DropApp;
import com.dropapp.R;
import com.dropapp.services.DropDetectionService;
import com.dropapp.services.EmailService;
import com.dropapp.util.Settings;
import com.dropapp.util.Util;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends Activity {

    private static final int SETUP_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.hasFinishedSetup(this)) {
            Intent i = new Intent(this, SetupActivity.class);
            startActivityForResult(i, SETUP_REQUEST_CODE);
        }

        final Button toggleServiceButton = (Button) this.findViewById(R.id.toggleServiceButton);
        toggleServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DropDetectionService.class);

                if (DropDetectionService.IS_RUNNING) {
                    stopService(i);
                    toggleServiceButton.setText("START");
                } else {
                    startService(i);
                    toggleServiceButton.setText("STOP");
                }
            }
        });

        if (DropDetectionService.IS_RUNNING) {
            toggleServiceButton.setText("STOP");
        }

        Button viewDataButton = (Button) this.findViewById(R.id.viewDataButton);
        viewDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DataViewActivity.class);
                startActivity(i);
            }
        });

        ImageButton settingsButton = (ImageButton) this.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        Tracker tracker = Util.getTracker(this);
        tracker.setScreenName("MainActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());


        //Email test
        EmailService m = new EmailService("dropappemailservice@gmail.com", "DropAppEmail");

        String[] toArr = {"kyleozhang@gmail.com.com", "chang101@aol.com"};
        m.setTo(toArr);
        m.setFrom("wooo@wooo.com");
        m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device.");
        m.setBody("Email body.");

        try {
            m.addAttachment("/sdcard/filelocation");
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email", e);
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETUP_REQUEST_CODE:
                if (resultCode == SetupActivity.SETUP_COMPLETE) {
                    Settings.setSetupFinished(this);
                }
                break;
        }
    }
}
