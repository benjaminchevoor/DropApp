package com.dropapp.logger;

import android.content.Context;

import com.dropapp.services.DropListener;

import java.io.IOException;

/**
 * Created by benjaminchevoor on 4/5/16.
 */
public class DropStateLogger extends Logger {

    public static void logTransition(Context context, DropListener.State state) throws IOException, LogWriteException {
        Logger.log(context, "Drop state transitioning to", state.toString());
    }

}
