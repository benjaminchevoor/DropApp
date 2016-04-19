package com.dropapp.notifications;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.dropapp.util.Settings;


/**
 * Created by benjaminchevoor on 4/4/16.
 * Edited by Josh Blanchette on 4/11/16.
 */
public class EmailHandler {

    /**
     * Called when an email should be sent to the user
     * JB: Definitely plan on using some sort of intent, otherwise we would have to write
     * our own client.
     * @param context
     */
    public static void notifyDropLocation(final Context context) {
        BackgroundMail.newBuilder(context)
                .withUsername("dropappemailservice@gmail.com")
                .withPassword("DropAppEmail")
                .withMailto(Settings.getEmail(context))
                .withSubject("Phone lost!")
                .withBody("this is the body") //TODO
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

}
