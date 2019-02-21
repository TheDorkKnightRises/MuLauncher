package com.mulauncher.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.User;
import com.mulauncher.models.User_;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class LockscreenLoginFragment extends Fragment {
    Button bt;
    EditText usernameView, passwordView;
    Box userBox;
    QueryBuilder builder;
    List<User> user;

    private Context context;
    private LockscreenLoginFragment.OnFragmentInteractionListener mListener;

    public LockscreenLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lockscreen_login, container, false);

        usernameView = view.findViewById(R.id.editTextUsername);
        passwordView = view.findViewById(R.id.editTextPassword);
        bt = view.findViewById(R.id.unlockButton);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBox = ((LauncherApplication) context.getApplicationContext()).getBoxStore().boxFor(User.class);
                builder = userBox.query();
                user = builder.equal(User_.username, usernameView.getText().toString())
                        .equal(User_.password, passwordView.getText().toString()).build().find();

                if (user.isEmpty()) {
                    usernameView.setText("");
                    passwordView.setText("");
                    Toast.makeText(LockscreenLoginFragment.this.context, "Invalid Credentials", Toast.LENGTH_LONG).show();
                } else {
                    String username = usernameView.getText().toString().trim();
                    SharedPreferences preferences = context.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    preferences.edit()
                            .putString(AppConstants.USER_NAME, username)
                            .apply();
                    mListener.onLoginSuccess(username);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLoginSuccess(String username);
    }

}
