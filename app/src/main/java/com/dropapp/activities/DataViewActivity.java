package com.dropapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.dropapp.R;
import com.dropapp.services.AccelerometerService;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DataViewActivity extends AppCompatActivity {

    private Button recordButton;
    private EditText fileNameEditText;
    private TextView outputTextView;
    private Bar xBar;
    private Bar yBar;
    private Bar zBar;

    private final AccelerometerService accelerometerService = new AccelerometerService();
    private boolean isResumed = false;

    private File file = null;
    private PrintWriter fileWriter = null;
    private long startRecordTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        this.outputTextView = (TextView) this.findViewById(R.id.outputTextView);

        final BarChartView barChartView = (BarChartView) this.findViewById(R.id.barChartView);
        barChartView.setBarBackgroundColor(Color.BLACK);
        barChartView.setBarSpacing(10);
        barChartView.setSetSpacing(10);
        barChartView.setLabelsColor(Color.WHITE);
        barChartView.setAxisColor(Color.GRAY);
        barChartView.setAxisBorderValues(-20, 20, 2);

        this.xBar = new Bar("x", 0.0f);
        this.yBar = new Bar("y", 0.0f);
        this.zBar = new Bar("z", 0.0f);
        this.xBar.setColor(Color.WHITE);
        this.yBar.setColor(Color.WHITE);
        this.zBar.setColor(Color.WHITE);

        BarSet barSet = new BarSet();
        barSet.addBar(xBar);
        barSet.addBar(yBar);
        barSet.addBar(zBar);
        barChartView.addData(barSet);
        barChartView.show();

        this.accelerometerService.setRawAccelerometerDataListener(new AccelerometerService.RawAccelerometerDataListener() {
            @Override
            public void newData(float x, float y, float z) {
                double avg = Math.sqrt((x * x) + (y * y) + (z * z));
                String format = String.format("%9.5f, %9.5f, %9.5f, %9.5f\n", x, y, z, avg);
//                outputTextView.append(format);

                xBar.setValue(x);
                yBar.setValue(y);
                zBar.setValue(z);
                barChartView.show();

                PrintWriter pw = fileWriter;
                if (pw != null) {
                    pw.write(format);
                }
            }
        });

        this.fileNameEditText = (EditText) this.findViewById(R.id.fileNameEditText);

        this.recordButton = (Button) this.findViewById(R.id.recordButton);
        this.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordButtonPressed();
            }
        });

        Button optionsButton = (Button) this.findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOptionsMenu();
            }
        });
    }

    private void recordButtonPressed() {
        if (this.fileWriter == null) {
            try {
                String fileName = this.fileNameEditText.getText().toString();
                if (!fileName.isEmpty()) {
                    if (!fileName.endsWith(".txt")) {
                        fileName += ".txt";
                    }

                    try {
                        File rootPath = Environment.getExternalStorageDirectory().getAbsoluteFile();
                        File folder = new File(rootPath, "dropapp");
                        if (!folder.exists() && !folder.mkdir()) {
                            throw new RuntimeException("Failed to make dropapp folder");
                        }

                        this.file = new File(folder, fileName);
                        this.fileWriter = new PrintWriter(this.file);
                        this.recordButton.setText("stop");

                        this.startRecordTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        Toast.makeText(DataViewActivity.this, "Failed to open file to write", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DataViewActivity.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(DataViewActivity.this, "Failed to toString() file name", Toast.LENGTH_LONG).show();
            }
        } else {
            this.recordButton.setText("record");
            try {
                long duration = System.currentTimeMillis() - startRecordTime;
                fileWriter.write("\n\nDuration: " + duration + "ms");

                PrintWriter fw = this.fileWriter;
                final File savedFile = this.file;

                this.fileWriter = null;
                this.file = null;

                fw.flush();
                fw.close();

                Snackbar.make(this.outputTextView, savedFile.getName() + " recorded", Snackbar.LENGTH_LONG)
                        .setAction("Share", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareFile(savedFile);
                            }
                        })
                        .show();
            } catch (Exception e) {
                Toast.makeText(DataViewActivity.this, "Failed to close file", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayOptionsMenu() {
        File rootDir = Environment.getExternalStorageDirectory();
        File folder = new File(rootDir, "dropapp");
        final File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            Toast.makeText(DataViewActivity.this, "No files to share", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DataViewActivity.this);
            builder.setTitle("Recorded files");

            List<String> fileNames = new ArrayList<>();
            for (File f : files) {
                fileNames.add(f.getName());
            }

            final AtomicReference<File> selectedFile = new AtomicReference<>(files[0]);
            builder.setSingleChoiceItems(fileNames.toArray(new String[files.length]), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedFile.set(files[which]);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final File deleteFile = selectedFile.get();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(DataViewActivity.this);

                    builder1.setMessage("Are you sure you want to delete " + deleteFile.getName() + "?");

                    builder1.setNegativeButton("Cancel", null);

                    builder1.setPositiveButton("Delete " + deleteFile.getName(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (deleteFile.delete()) {
                                Toast.makeText(DataViewActivity.this, "File deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DataViewActivity.this, "Failed to delete file", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    builder1.create().show();
                }
            });
            builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shareFile(selectedFile.get());
                }
            });

            builder.create().show();
        }
    }

    private void shareFile(File file) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Sharing..."));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //todo
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //todo
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            this.accelerometerService.initialize(null, this);
            this.outputTextView.setText("Accelerometer found. Starting...\n");
            this.outputTextView.append(String.format("%9s, %9s, %9s, %9s\n", "x", "y", "z", "avg"));
        } catch (AccelerometerService.NoAccelerometerSensorException e) {
            this.outputTextView.setText("Error! No accelerometer available.");
        }

        this.isResumed = true;

        //forces the scroll view to the bottom
        final ScrollView scrollView = (ScrollView) this.findViewById(R.id.scrollView);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isResumed) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.accelerometerService.uninitialize(this);

        this.isResumed = false;
    }

}
