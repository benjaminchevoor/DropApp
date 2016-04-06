package com.dropapp.logger;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by benjaminchevoor on 4/5/16.
 */
public class Logger {

    public static final String BROADCAST = "dropapp.logBroadcast";
    public static final String BROADCAST_ARGS = "dropapp.logBroadcast.params";

    protected static class LogWriteException extends Exception {
        public LogWriteException(String detailMessage) {
            super(detailMessage);
        }
    }

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    protected static File OUTPUT_FILE;

    public static synchronized void log(Context context, String... strings) throws LogWriteException, IOException {
        if (OUTPUT_FILE == null) {
            throw new LogWriteException("Logging not start");
        }

        Intent i = new Intent(BROADCAST);
        i.putExtra(BROADCAST_ARGS, strings);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);

        Writer pw = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_FILE, true)));
        pw.write(DATE_FORMAT.format(new GregorianCalendar().getTime()));
        pw.write(" - ");
        pw.write(Thread.currentThread().getName() + "/" + Thread.currentThread().getId());
        if (strings != null) {
            for (String s : strings) {
                pw.write(" - ");
                pw.write(s == null ? "null" : s);
            }
        }
        pw.write("\n");
        pw.flush();
        pw.close();
    }

    public static void start() throws LogWriteException, IOException {
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dropapp";
        File folder = new File(folderPath);

        if (!folder.exists() && !folder.mkdirs()) {
            throw new LogWriteException("Failed to create folder");
        }

        File file = new File(folder, DATE_FORMAT.format(new GregorianCalendar().getTime()).concat(".txt"));

        Writer pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        pw.write("");
        pw.flush();
        pw.close();

        OUTPUT_FILE = file;
    }

    public static void stop() {
        OUTPUT_FILE = null;
    }

}
