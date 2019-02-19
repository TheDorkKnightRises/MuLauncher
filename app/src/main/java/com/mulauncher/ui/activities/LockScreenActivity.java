package com.mulauncher.ui.activities;

import android.app.KeyguardManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.mulauncher.R;
import com.mulauncher.ui.fragments.LockscreenClockFragment;
import com.mulauncher.ui.fragments.LockscreenLoginFragment;

public class LockScreenActivity extends AppCompatActivity implements LockscreenLoginFragment.OnFragmentInteractionListener {

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
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonymanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        setContentView(R.layout.activity_lock_screen_app);
        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(new SwipePagerAdapter(getSupportFragmentManager()));

    }

    @Override
    public void onLoginSuccess(String username) {
        // TODO: User has logged in, now check which profile is appropriate for loading
        finish();
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

    private class SwipePagerAdapter extends FragmentPagerAdapter {

        SwipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return new LockscreenClockFragment();
                default:
                    return new LockscreenLoginFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
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


