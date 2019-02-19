package com.mulauncher.ui.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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

public class LockScreenActivity extends AppCompatActivity {
    Button bt;
    EditText username, password;
    Box userBox;
    QueryBuilder builder;
    List<User> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow can be put over here

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        try {
            // disable keyguard
            disableKeyguard();

        } catch (Exception e) {
            e.printStackTrace();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonymanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        setContentView(R.layout.activity_lock_screen_app);

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        bt = findViewById(R.id.unlockButton);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBox = ((LauncherApplication) getApplication()).getBoxStore().boxFor(User.class);
                builder = userBox.query();
                builder.equal(User_.username, username.getText().toString())
                        .equal(User_.password, password.getText().toString());

                user = builder.build().find();

                if (user.isEmpty()) {
                    username.setText("");
                    password.setText("");
                    Toast.makeText(LockScreenActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    preferences.edit()
                            .putString(AppConstants.USER_NAME, username.getText().toString().trim())
                            .apply();
                    finish();
                }
            }
        });

    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        return (keyCode == KeyEvent.KEYCODE_HOME);

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        return (event.getKeyCode() == KeyEvent.KEYCODE_HOME);
    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }

    class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Finish lock screen activity
                    finish();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }
}


