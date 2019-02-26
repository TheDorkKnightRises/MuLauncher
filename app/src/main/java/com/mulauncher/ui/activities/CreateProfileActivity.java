package com.mulauncher.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.R;
import com.mulauncher.interfaces.AppGenreChecklistInterface;
import com.mulauncher.models.Profile;
import com.mulauncher.ui.adapters.AppGenreAdapter;

import java.util.List;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity implements AppGenreChecklistInterface {

    EditText profilename;
    FloatingActionButton button;
    RecyclerView appListRecyclerView;
    SharedPreferences user_preferences;
    String username;
    List<String> appGenreList;
    Map<String, Integer> appGenreMap;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        profilename = findViewById(R.id.profile_edittext);
        button = findViewById(R.id.done_button);
        profile = new Profile();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String profileName = profilename.getText().toString().trim();

                if (profileName.isEmpty()) {
                    Toast.makeText(CreateProfileActivity.this, getString(R.string.profile_name_empty_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle extras = getIntent().getExtras();

                if (extras == null) {
                    Toast.makeText(CreateProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                username = extras.getString(AppConstants.USER_NAME, "");
                profile.setProfileName(profileName);
                profile.setUsername(username);

                if (appGenreList.isEmpty()) {
                    Intent i = new Intent(CreateProfileActivity.this, AppsSelectionActivity.class);
                    i.putExtra("ProfileObject", profile);
                    startActivity(i);
                } else {
                    Intent i = new Intent(CreateProfileActivity.this, LocationActivity.class);
                    i.putExtra("ProfileObject", profile);
                    startActivity(i);
                }

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppGenreAdapter(this, this));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    }

    @Override
    public void setAppGenreList(Map<String, Integer> appGenreMap) {
        this.appGenreMap = appGenreMap;
    }
}
