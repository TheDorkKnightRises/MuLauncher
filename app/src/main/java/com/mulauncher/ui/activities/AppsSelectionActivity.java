package com.mulauncher.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mulauncher.R;
import com.mulauncher.interfaces.AppChecklistInterface;
import com.mulauncher.models.Profile;
import com.mulauncher.models.SelectedAppInfo;
import com.mulauncher.ui.adapters.AppChecklistAdapter;

import java.util.List;

public class AppsSelectionActivity extends AppCompatActivity implements AppChecklistInterface {

    List<SelectedAppInfo> AppList;
    FloatingActionButton done;
    Boolean first;
    String apps_package;
    RecyclerView appListRecyclerView;

    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_selection);

        profile = (Profile) getIntent().getSerializableExtra("ProfileObject");

        first = true;
        done = findViewById(R.id.done_button);
        appListRecyclerView = findViewById(R.id.appListRecyclerView);

        appListRecyclerView.setAdapter(new AppChecklistAdapter(this, this));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                profile.setAppsPackageList(apps_package);

                Intent i = new Intent(AppsSelectionActivity.this, LocationActivity.class);
                i.putExtra("ProfileObject", profile);
                startActivity(i);
            }
        });
    }

    @Override
    public void setAppList(List<SelectedAppInfo> AppList) {
        this.AppList = AppList;
    }
}
