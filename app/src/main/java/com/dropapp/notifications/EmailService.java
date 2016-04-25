package com.dropapp.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

public class EmailService extends IntentService implements LocationListener {

    private static final String ACTION_EMAIL_GPS_COORDS = "emailAction";

    private Context context;

    public EmailService() {
        super("EmailService");
    }

    public static void emailUserGpsCoordinates(Context context) {
        Intent intent = new Intent(context, EmailService.class);
        intent.setAction(ACTION_EMAIL_GPS_COORDS);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = this.getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EMAIL_GPS_COORDS.equals(action)) {

                LocationManager locationManager = (LocationManager)
                        context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestSingleUpdate(locationManager.GPS_PROVIDER, this, this.context.getMainLooper());

            }
        }
    }


    @Override
    public void onLocationChanged(Location loc) {
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();

        BackgroundMail.newBuilder(context)
                .withUsername("dropappemailservice@gmail.com")
                .withPassword("DropAppEmail")
                .withMailto("barrybostwick@gmail.com")
                .withSubject("Phone lost!")
                .withBody("Here is a link to where your phone is. " +
                        "http://maps.google.com/maps?q=loc:" +latitude +"," +longitude)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Email sent!", Toast.LENGTH_SHORT).show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Toast.makeText(context, "Email failed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .send();
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
}
