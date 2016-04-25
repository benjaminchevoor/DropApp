package com.dropapp.notifications;

import android.content.Context;


/**
 * Created by benjaminchevoor on 4/4/16.
 * Edited by Josh Blanchette on 4/11/16.
 */
public class EmailHandler {

    public static void notifyDropLocation(final Context context) {
        EmailService.emailUserGpsCoordinates(context);
    }

}
