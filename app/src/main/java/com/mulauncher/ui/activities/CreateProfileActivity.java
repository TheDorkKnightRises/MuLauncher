package com.mulauncher.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.interfaces.AppGenreChecklistInterface;
import com.mulauncher.interfaces.OnGenreFetchListener;
import com.mulauncher.models.AppGenre;
import com.mulauncher.models.AppGenre_;
import com.mulauncher.models.Profile;
import com.mulauncher.ui.adapters.AppGenreAdapter;
import com.mulauncher.util.FetchCategoryTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

public class CreateProfileActivity extends AppCompatActivity
        implements AppGenreChecklistInterface
        , OnGenreFetchListener {

    EditText profilename;
    FloatingActionButton button;
    RecyclerView appListRecyclerView;
    SharedPreferences user_preferences;
    String username;
    List<String> appGenreList;
    Map<String, Integer> appGenreMap;
    Profile profile;
    String apps_package;
    PackageManager pm;
    Boolean first = true;
    Box genreBox;
    FetchCategoryTask fetchCategoryTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_profile);

        genreBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(AppGenre.class);
        appGenreList = new ArrayList<>();

        appListRecyclerView = findViewById(R.id.appListRecyclerView);

        profilename = findViewById(R.id.profile_edittext);
        button = findViewById(R.id.done_button);
        profile = new Profile();

        pm = CreateProfileActivity.this.getPackageManager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.progress_msg);
        progressDialog.create();
        progressDialog.show();
        fetchCategoryTask = new FetchCategoryTask(this, this);
        fetchCategoryTask.execute();

        findViewById(R.id.manualSelectionButton).setOnClickListener(new View.OnClickListener() {
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

                Intent i = new Intent(CreateProfileActivity.this, AppsSelectionActivity.class);
                i.putExtra("ProfileObject", profile);
                i.putExtra(AppConstants.USER_NAME, username);
                startActivity(i);
                finish();
            }
        });

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

                for (Map.Entry<String, Integer> e : appGenreMap.entrySet())
                    if (e.getValue() == 1) {
                        Log.d("Selected", e.getKey());
                        appGenreList.add(e.getKey());
                    }

                if (appGenreList.isEmpty()) {
                    Intent i = new Intent(CreateProfileActivity.this, AppsSelectionActivity.class);
                    i.putExtra("ProfileObject", profile);
                    i.putExtra(AppConstants.USER_NAME, username);
                    startActivity(i);
                } else {

                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                    for (ApplicationInfo s : packages) {
                        AppGenre a = (AppGenre) (genreBox.query().equal(AppGenre_.appPackage, s.packageName).build().findFirst());

                        if (appGenreList.contains(a.getGenre())) {
                            if (first) {
                                apps_package = s.packageName;
                                first = false;
                            } else {
                                apps_package = apps_package.concat(" " + s.packageName);
                            }
                        }
                    }
                    profile.setAppsPackageList(apps_package);

                    Intent i = new Intent(CreateProfileActivity.this, LocationActivity.class);
                    i.putExtra("ProfileObject", profile);
                    startActivity(i);
                }

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    @Override
    public void setAppGenreList(Map<String, Integer> appGenreMap) {
        this.appGenreMap = appGenreMap;
    }

    @Override
    public void onGenreFetch() {
        if (progressDialog.isShowing())
            progressDialog.cancel();
        appListRecyclerView.setAdapter(new AppGenreAdapter(this, this));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    }
}
