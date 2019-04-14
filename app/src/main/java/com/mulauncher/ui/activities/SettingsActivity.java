package com.mulauncher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.User;
import com.mulauncher.models.User_;

import io.objectbox.Box;

public class SettingsActivity extends AppCompatActivity {
    int listType;
    View appListPreferenceView;
    TextView appListPreferenceTextView;
    SharedPreferences preferences, userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(AppConstants.APP_PREFERENCES, MODE_PRIVATE);

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

        listType = preferences.getInt(AppConstants.APP_LIST_TYPE, 0);
        String listTypeString = (listType == 0) ? getString(R.string.list) : getString(R.string.grid);
        appListPreferenceView = findViewById(R.id.app_list_display);
        appListPreferenceTextView = findViewById(R.id.app_list_text);
        appListPreferenceTextView.setText(listTypeString);
        appListPreferenceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listType = listType ^ 1;
                String listTypeString = (listType == 0) ? getString(R.string.list) : getString(R.string.grid);
                appListPreferenceTextView.setText(listTypeString);
                preferences.edit().putInt(AppConstants.APP_LIST_TYPE, listType).apply();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        userPreferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);

        Box userBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(User.class);
        User currentUser = (User) userBox.query().equal(User_.username,
                userPreferences.getString(AppConstants.USER_NAME, ""))
                .build()
                .findFirst();
        if (!currentUser.isAdmin()) {
            findViewById(R.id.manage_users).setVisibility(View.GONE);
        } else {
            findViewById(R.id.manage_users).setVisibility(View.VISIBLE);
            findViewById(R.id.manage_users).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this, ManageUsersActivity.class));
                }
            });
        }

    }
}
