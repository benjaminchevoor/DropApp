package com.dropapp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.dropapp.R;
import com.dropapp.activities.MainActivity;

/**
 * Created by benjaminchevoor on 4/4/16.
 */
public class AlarmHandler {

    private static final int NOTIFICATION_ID = 1;

    /*pkg*/ static void raiseAlarm(Context context) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentTitle("AHH PHONE DROPPED")
                        .setContentText("Pick me up pick me up!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_warning);
        mBuilder.setVibrate(new long[] {0, 1000, 500, 1000, 500, 1000});

        Intent i = new Intent(NotificationActionReceiver.ACTION_CLEAR_DROP);
        i.putExtra(NotificationActionReceiver.EXTRA_ALARM, true);
        PendingIntent clear = PendingIntent.getBroadcast(context, NotificationActionReceiver.REQUEST_CODE, i, 0);
        mBuilder.addAction(R.drawable.ic_notifications_black_24dp, "Stop", clear);

        mBuilder.setDeleteIntent(clear);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /*pkg*/ static void clearAlarm(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

}
