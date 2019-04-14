package com.mulauncher.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.Profile;
import com.mulauncher.models.Profile_;
import com.mulauncher.ui.adapters.ProfileAdapter;

import java.util.List;

import io.objectbox.Box;

public class ManageProfilesActivity extends AppCompatActivity {
    RecyclerView profilesRecyclerView;
    Box profileBox;
    List<Profile> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profiles);
        profileBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(Profile.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);
        profileList = profileBox.query().equal(Profile_.username, preferences.getString(AppConstants.USER_NAME, "")).build().find();
        for (Profile p : profileList)
            Log.d("Profile", p.getProfileName() + "\n");
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        profilesRecyclerView.setAdapter(new ProfileAdapter(this, profileList));
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
