package com.mulauncher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.R;

public class SettingsActivity extends AppCompatActivity {
    int listType;
    TextView appListPreferenceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.manage_profiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ManageProfilesActivity.class));
            }
        });

        findViewById(R.id.add_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, CreateProfileActivity.class);
                String username = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE)
                        .getString(AppConstants.USER_NAME, getString(R.string.user));
                i.putExtra(AppConstants.USER_NAME, username);
                startActivity(i);
            }
        });

        findViewById(R.id.geofencing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LocationActivity.class));
            }
        });
        findViewById(R.id.add_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AddUserActivity.class));
            }
        });

        final SharedPreferences preferences = getSharedPreferences(AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        listType = preferences.getInt(AppConstants.APP_LIST_TYPE, 0);
        String listTypeString = (listType == 1) ? getString(R.string.list) : getString(R.string.grid);
        appListPreferenceTextView = findViewById(R.id.app_list_display);
        appListPreferenceTextView.setText(getString(R.string.display_apps_as, listTypeString));
        appListPreferenceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listType = listType ^ 1;
                String listTypeString = (listType == 1) ? getString(R.string.list) : getString(R.string.grid);
                appListPreferenceTextView.setText(getString(R.string.display_apps_as, listTypeString));
                preferences.edit().putInt(AppConstants.APP_LIST_TYPE, listType).apply();

            }
        });

    }

}
