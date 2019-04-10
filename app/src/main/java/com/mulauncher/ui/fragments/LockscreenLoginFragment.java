package com.mulauncher.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

public class LockscreenLoginFragment extends Fragment implements PatternView.OnPatternListener {
    Button bt, unlockModeButton;
    EditText usernameView, passwordView;
    Box userBox;
    QueryBuilder builder;
    List<User> user;
    boolean isPatternViewVisible = true;
    PatternView patternView;
    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            if (patternView != null)
                patternView.clearPattern();
        }
    };

    private Context context;
    private LockscreenLoginFragment.OnFragmentInteractionListener mListener;

    public LockscreenLoginFragment() {
        // Required empty public constructor
    }

    String username;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lockscreen_login, container, false);

        usernameView = view.findViewById(R.id.editTextUsername);
        passwordView = view.findViewById(R.id.editTextPassword);
        bt = view.findViewById(R.id.unlockButton);
        unlockModeButton = view.findViewById(R.id.unlockModeButton);
        patternView = view.findViewById(R.id.patternView);

        patternView.setOnPatternListener(this);

        unlockModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPatternViewVisible) {
                    view.findViewById(R.id.credentialsLayout).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.patternView).setVisibility(View.GONE);
                    unlockModeButton.setText(R.string.use_pattern_to_unlock);
                    isPatternViewVisible = false;
                } else {
                    view.findViewById(R.id.credentialsLayout).setVisibility(View.GONE);
                    view.findViewById(R.id.patternView).setVisibility(View.VISIBLE);
                    unlockModeButton.setText(R.string.switch_user);
                    isPatternViewVisible = true;
                }
            }
        });

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
    public void onPatternStart() {
        patternView.removeCallbacks(clearPatternRunnable);
        patternView.setDisplayMode(PatternView.DisplayMode.Correct);
    }

    @Override
    public void onPatternCleared() {
        patternView.removeCallbacks(clearPatternRunnable);
    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> pattern) {
        if (isPatternCorrect(pattern)) {
            mListener.onLoginSuccess(username);
        } else {
            Toast.makeText(context, R.string.pl_wrong_pattern, Toast.LENGTH_SHORT).show();
            patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
            patternView.removeCallbacks(clearPatternRunnable);
            patternView.postDelayed(clearPatternRunnable, 2000);
            // TODO: Count number of failed attempts and disallow after specific limit
        }
    }

    public interface OnFragmentInteractionListener {
        void onLoginSuccess(String username);
    }

    private boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        SharedPreferences userPreference = context.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        username = userPreference.getString(AppConstants.USER_NAME, "");

        userBox = ((LauncherApplication) context.getApplicationContext()).getBoxStore().boxFor(User.class);
        builder = userBox.query();
        user = builder.equal(User_.username, username).equal(User_.pattern, PatternUtils.patternToSha1String(pattern)).build().find();
        Log.d("Pattern SHA1", PatternUtils.patternToSha1String(pattern));

        return !user.isEmpty();
    }


}
