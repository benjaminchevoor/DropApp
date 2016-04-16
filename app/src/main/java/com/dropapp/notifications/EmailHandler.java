package com.dropapp.notifications;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
    public static void notifyDropLocation(Context context) {
        /*String to = "Reciever email here";
        String from = "Sender email here";*/
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{Settings.getEmail(context)});
        i.putExtra(Intent.EXTRA_SUBJECT, "DropApp Notification");
        i.putExtra(Intent.EXTRA_TEXT   , "Hello DropApp user.  You have dropped your phone" +
                "here.  Please trace your steps so you can get your phone back as soon as possible.");
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
