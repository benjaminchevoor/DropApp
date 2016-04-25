package com.dropapp.notifications;

import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.dropapp.util.Settings;


/**
 * Created by benjaminchevoor on 4/4/16.
 * Edited by Josh Blanchette on 4/11/16.
 */
public class EmailHandler implements LocationListener {

    /**
     * Called when an email should be sent to the user
     * JB: Definitely plan on using some sort of intent, otherwise we would have to write
     * our own client.
     *
     * @param context
     */
    static double longitude;
    static double latitude;

    @Override
    public void onLocationChanged(Location loc) {
         longitude = loc.getLongitude();
         latitude = loc.getLatitude();
    }


    public static void notifyDropLocation(final Context context){


        BackgroundMail.newBuilder(context)
                .withUsername("dropappemailservice@gmail.com")
                .withPassword("DropAppEmail")
                .withMailto(Settings.getEmail(context))
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
