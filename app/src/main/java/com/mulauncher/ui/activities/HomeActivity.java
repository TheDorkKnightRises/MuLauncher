package com.mulauncher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.R;
import com.mulauncher.services.LockScreenService;
import com.mulauncher.ui.adapters.AppListAdapter;
import com.mulauncher.ui.adapters.AppUsageListAdapter;

public class HomeActivity extends AppCompatActivity {
    RecyclerView appListRecyclerView, appUsageListRecyclerView;
    ImageButton settingsButton;
    SharedPreferences app_preferences, user_preferences, profile_preference;
    int gridSpan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app_preferences = getSharedPreferences(AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        user_preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);

        if (app_preferences.getBoolean(AppConstants.FIRST_LAUNCH, true)) {
            Intent intent = new Intent(HomeActivity.this, AppTourActivity.class);
            startActivity(intent);
            app_preferences.edit().putBoolean(AppConstants.FIRST_LAUNCH, false).apply();
        }

        getWindow().addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED/*|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON*/);

        Intent serviceIntent = new Intent(this, LockScreenService.class);
        startService(serviceIntent);

        setContentView(R.layout.activity_home);

        appUsageListRecyclerView = findViewById(R.id.mostUsedAppListRecyclerView);
        appListRecyclerView = findViewById(R.id.appListRecyclerView);

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
        String profile = user_preferences.getString(username + AppConstants.USER_LAST_PROFILE, getString(R.string.def));
        if (!"".equals(username)) {
            ((TextView) findViewById(R.id.welcome_header)).setText(getString(R.string.welcome_comma, username));
            ((TextView) findViewById(R.id.profile_header)).setText(profile);
        }

        appUsageListRecyclerView = findViewById(R.id.mostUsedAppListRecyclerView);
        appUsageListRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        AppUsageListAdapter appUsageListAdapter = new AppUsageListAdapter(this);
        appUsageListRecyclerView.setAdapter(appUsageListAdapter);
        if (appUsageListAdapter.getItemCount() != 0) {
            findViewById(R.id.most_used_apps_prompt).setVisibility(View.GONE);
        }

        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppListAdapter(this, AppListAdapter.TYPE_LIST));

        int span = getSharedPreferences(AppConstants.APP_PREFERENCES, MODE_PRIVATE).getInt(AppConstants.APP_LIST_TYPE, 0);
        if (gridSpan != span + 1) {
            gridSpan = span + 1;
            appListRecyclerView.setLayoutManager(new GridLayoutManager(this, gridSpan));
        }
    }

    @Override
    public void onBackPressed() {

    }


}
