package com.mulauncher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.Profile;
import com.mulauncher.models.User;
import com.mulauncher.models.User_;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.ToOne;

public class CreateProfileActivity extends AppCompatActivity {

    EditText profilename;
    TextView done, locationButton;
    SharedPreferences user_preferences;
    String username;
    Profile profile;
    ToOne<User> user;
    Box userBox, profileBox;
    QueryBuilder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        user_preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);
        profilename = findViewById(R.id.profile_edittext);
        done = findViewById(R.id.done_button);
        locationButton = findViewById(R.id.location_button);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = user_preferences.getString(AppConstants.USER_NAME, getString(R.string.user));
                userBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(User.class);

                profile = new Profile();
                profile.setProfileName(profilename.getText().toString());
                builder = userBox.query();
                user = (ToOne<User>) builder.equal(User_.username, username).build().findFirst();
                profile.setUser(user);
                profile.setId(0);

                profileBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(Profile.class);
                profileBox.put(profile);

                Intent i = new Intent(CreateProfileActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateProfileActivity.this, LocationActivity.class));
            }
        });

    }
}
