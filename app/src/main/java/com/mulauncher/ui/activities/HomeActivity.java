package com.mulauncher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.R;
import com.mulauncher.ui.adapters.AppListAdapter;

public class HomeActivity extends AppCompatActivity {
    RecyclerView appListRecyclerView;
    SharedPreferences app_preferences, user_preferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app_preferences = getSharedPreferences(AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        user_preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);

        if (app_preferences.getBoolean(AppConstants.FIRST_LAUNCH, true)) {
            Intent intent = new Intent(HomeActivity.this, AppTourActivity.class);
            startActivity(intent);
            app_preferences.edit().putBoolean(AppConstants.FIRST_LAUNCH, false).apply();
        }

        setContentView(R.layout.activity_home);


        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppListAdapter(this, AppListAdapter.TYPE_LIST));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = user_preferences.getString(AppConstants.USER_NAME, "");
        if (!"".equals(username)) {
            ((TextView) findViewById(R.id.user_name)).setText(username);
        }
    }
}
