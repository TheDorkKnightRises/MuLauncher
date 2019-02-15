package com.mulauncher.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.User;
import com.mulauncher.ui.activities.FaceRegistration;
import com.mulauncher.ui.activities.FingerprintRegistration;

import io.objectbox.Box;

public class AddUserDetailsFragment extends Fragment {
    Context context;
    EditText usernameEditText, passwordEditText; //, confirmPasswordEditText;
    TextView face_lock, fingerprint;
    Box userBox;
    public AddUserDetailsFragment() {
        // Required empty public constructor
    }

    public static AddUserDetailsFragment newInstance(Context context) {
        AddUserDetailsFragment addUserDetailsFragment = new AddUserDetailsFragment();
        addUserDetailsFragment.context = context;
        return addUserDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user_details, container, false);

        usernameEditText = view.findViewById(R.id.name_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        face_lock = view.findViewById(R.id.button_face_lock);
        fingerprint = view.findViewById(R.id.button_fingerprint);
        //confirmPasswordEditText = view.findViewById(R.id.confirm_password_edittext);

        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddUserDetailsFragment.this.context, FingerprintRegistration.class);
                startActivity(i);
            }
        });
        face_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddUserDetailsFragment.this.context, FaceRegistration.class);
                startActivity(i);
            }
        });

        return view;
    }

    public void saveDetails() {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        if (!usernameEditText.getText().toString().trim().isEmpty()) {
            preferences.edit()
                    .putString(AppConstants.USER_NAME, usernameEditText.getText().toString().trim())
                    .apply();
            //create database here
            userBox = ((LauncherApplication) context.getApplicationContext()).getBoxStore().boxFor(User.class);
            userBox.put(new User(0, usernameEditText.getText().toString(), passwordEditText.getText().toString()));
        }
    }

}
