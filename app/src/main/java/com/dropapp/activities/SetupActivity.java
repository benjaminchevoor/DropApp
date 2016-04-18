package com.dropapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.dropapp.R;

public class SetupActivity extends AppCompatActivity {

    public static final int SETUP_COMPLETE = 1;

    private enum Page {
        EMAIL,
        CALIBRATION
    }

    private Page currentPage = Page.EMAIL;

    //views
    private ViewFlipper viewFlipper;
    private Button buttonLeft;
    private Button buttonRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        this.viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        this.buttonLeft = (Button) this.findViewById(R.id.buttonLeft);
        this.buttonRight = (Button) this.findViewById(R.id.buttonRight);

        updateButtons();
    }

    private void updateButtons() {
        switch (this.currentPage) {
            case EMAIL:
                this.buttonLeft.setText("Skip setup");
                this.buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                this.buttonRight.setText("Next");
                this.buttonRight.setEnabled(true);
                this.buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Next screen comes in from right.
                        viewFlipper.setInAnimation(SetupActivity.this, R.anim.slide_in_from_right);
                        // Current screen goes out from left.
                        viewFlipper.setOutAnimation(SetupActivity.this, R.anim.slide_out_to_left);
                        viewFlipper.showNext();

                        currentPage = Page.CALIBRATION;
                        updateButtons();
                    }
                });
                break;

            case CALIBRATION:
                this.buttonLeft.setText("Back");
                this.buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Next screen comes in from left.
                        viewFlipper.setInAnimation(SetupActivity.this, R.anim.slide_in_from_left);
                        // Current screen goes out from right.
                        viewFlipper.setOutAnimation(SetupActivity.this, R.anim.slide_out_to_right);
                        viewFlipper.showPrevious();

                        currentPage = Page.EMAIL;
                        updateButtons();
                    }
                });

                this.buttonRight.setText("Finish");
                this.buttonRight.setEnabled(false);
                this.buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(SetupActivity.SETUP_COMPLETE);
                        finish();
                    }
                });
        }
    }

    public void calibrationFinished() {
        if (this.currentPage == Page.CALIBRATION) {
            this.buttonRight.setEnabled(true);
        }
    }
}
