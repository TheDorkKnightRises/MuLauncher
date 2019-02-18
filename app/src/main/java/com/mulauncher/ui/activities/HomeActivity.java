package com.mulauncher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.R;
import com.mulauncher.services.LockScreenService;
import com.mulauncher.ui.adapters.AppListAdapter;

public class HomeActivity extends AppCompatActivity {
    RecyclerView appListRecyclerView;
    ImageButton settingsButton;
    SharedPreferences app_preferences, user_preferences, profile_preference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app_preferences = getSharedPreferences(AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        user_preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);
        profile_preference = getSharedPreferences(AppConstants.PROFILE, MODE_PRIVATE);

        if (app_preferences.getBoolean(AppConstants.FIRST_LAUNCH, true)) {
            Intent intent = new Intent(HomeActivity.this, AppTourActivity.class);
            startActivity(intent);
            Log.d("First launch", "true");
            app_preferences.edit().putBoolean(AppConstants.FIRST_LAUNCH, false).apply();
        }

        getWindow().addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED/*|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON*/);

        Intent serviceIntent = new Intent(this, LockScreenService.class);
        startService(serviceIntent);

        setContentView(R.layout.activity_home);


        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppListAdapter(this, AppListAdapter.TYPE_LIST));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        String username = user_preferences.getString(AppConstants.USER_NAME, getString(R.string.user));
        String profile = profile_preference.getString(AppConstants.PROFILE, getString(R.string.def));
        if (!"".equals(username)) {
            ((TextView) findViewById(R.id.welcome_header)).setText(getString(R.string.welcome_comma, username, profile));
        }
    }

    @Override
    public void onBackPressed() {

    }


}
