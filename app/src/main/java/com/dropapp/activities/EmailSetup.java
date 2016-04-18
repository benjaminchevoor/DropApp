package com.dropapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.dropapp.R;
import com.dropapp.util.Settings;


public class EmailSetup extends Fragment {

    public EmailSetup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EmailSetup.
     */
    public static EmailSetup newInstance() {
        return new EmailSetup();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_email_setup, container, false);

        final EditText emailEditText = (EditText) parentView.findViewById(R.id.emailEditText);
        emailEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Settings.setEmail(v.getContext(), emailEditText.getText().toString());
                return false;
            }
        });

        CheckBox enableEmailNotifications = (CheckBox) parentView.findViewById(R.id.enableEmailNotificationsCheckbox);
        enableEmailNotifications.setChecked(Settings.isEmailNotificationsEnabled(this.getContext()));
        enableEmailNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.setEnableEmailNotification(buttonView.getContext(), isChecked);
            }
        });

        return parentView;
    }
}
