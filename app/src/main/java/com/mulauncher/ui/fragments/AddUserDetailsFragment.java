package com.mulauncher.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mulauncher.AppConstants;
import com.mulauncher.R;

public class AddUserDetailsFragment extends Fragment {
    Context context;
    EditText usernameEditText, passwordEditText; //, confirmPasswordEditText;

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
        //confirmPasswordEditText = view.findViewById(R.id.confirm_password_edittext);

        return view;
    }

    public void saveDetails() {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        if (!usernameEditText.getText().toString().trim().isEmpty()) {
            preferences.edit()
                    .putString(AppConstants.USER_NAME, usernameEditText.getText().toString().trim())
                    .apply();
        }
    }

}
