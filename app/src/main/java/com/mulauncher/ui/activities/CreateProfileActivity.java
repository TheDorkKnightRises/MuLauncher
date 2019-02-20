package com.mulauncher.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.interfaces.AppChecklistInterface;
import com.mulauncher.models.Profile;
import com.mulauncher.models.SelectedAppInfo;
import com.mulauncher.ui.adapters.AppChecklistAdapter;

import java.util.List;

import io.objectbox.Box;

public class CreateProfileActivity extends AppCompatActivity implements AppChecklistInterface {

    EditText profilename;
    TextView done, locationButton;
    RecyclerView appListRecyclerView;
    SharedPreferences user_preferences, profile_preference;
    String username, apps_package;
    Profile profile;
    Box profileBox;
    Boolean first;
    List<SelectedAppInfo> AppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        first = true;
        profile_preference = getSharedPreferences(AppConstants.PROFILE, MODE_PRIVATE);

        profilename = findViewById(R.id.profile_edittext);
        done = findViewById(R.id.done_button);
        locationButton = findViewById(R.id.location_button);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String profileName = profilename.getText().toString().trim();
                if (profileName.isEmpty()) {
                    Toast.makeText(CreateProfileActivity.this, getString(R.string.profile_name_empty_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                for (SelectedAppInfo s : AppList) {
                    if (s.isSelected()) {
                        if (first) {
                            apps_package = s.getAppInfo().getPackageName().toString();
                            first = false;
                        } else {
                            apps_package = apps_package.concat(" " + s.getAppInfo().getPackageName().toString());
                        }
                        Log.d("CheckedApps", s.getAppInfo().getLabel() + "\n");
                    }
                }

                Log.d("Apps_in_Profile", apps_package);
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    Toast.makeText(CreateProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                username = extras.getString(AppConstants.USER_NAME, "");
                profile = new Profile();
                profile.setProfileName(profileName);
                profile.setUsername(username);
                profile.setAppsPackageList(apps_package);
                profile.setId(0);
                profileBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(Profile.class);
                profileBox.put(profile);

                profile_preference.edit().putString(AppConstants.PROFILE, profile.getProfileName()).apply();

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateProfileActivity.this, LocationActivity.class));
            }
        });

        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppChecklistAdapter(this, this));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));


    }

    @Override
    public void setAppList(List<SelectedAppInfo> AppList) {
        this.AppList = AppList;
    }
}
