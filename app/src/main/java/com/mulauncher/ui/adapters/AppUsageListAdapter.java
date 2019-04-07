package com.mulauncher.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.AppCount;
import com.mulauncher.models.AppCount_;
import com.mulauncher.models.AppInfo;
import com.mulauncher.models.Profile;
import com.mulauncher.models.Profile_;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class AppUsageListAdapter extends RecyclerView.Adapter<AppUsageListAdapter.ViewHolder> {
    Box appCountBox;
    String username;
    Profile profile;
    String profileName;
    private List<AppInfo> appsList;
    private int type;

    public AppUsageListAdapter(Context c) {

        //This is where we build our list of app details, using the app
        //object we created to store the label, package name and icon
        PackageManager pm = c.getPackageManager();
        Box profileBox;
        String[] packages;
        List<String> packagelist;
        List<Profile> proList;
        SharedPreferences userpref = c.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        username = userpref.getString(AppConstants.USER_NAME, "");
        appsList = new ArrayList<>();
        appCountBox = ((LauncherApplication) c.getApplicationContext()).getBoxStore().boxFor(AppCount.class);
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);

        if (!"".equals(userpref.getString(username + AppConstants.USER_LAST_PROFILE, ""))) {
            profileBox = ((LauncherApplication) c.getApplicationContext()).getBoxStore().boxFor(Profile.class);

            proList = profileBox.getAll();
            for (Profile p : proList)
                Log.d("AllProfiles", p.getProfileName() + " " + p.getUsername() + " " + p.getAppsPackageList());

            profile = (Profile) profileBox.query().equal(Profile_.profileName, userpref.getString(username + AppConstants.USER_LAST_PROFILE, ""))
                    .equal(Profile_.username, username)
                    .build().findFirst();
            profileName = profile.getProfileName();

            String profiles = profile.getAppsPackageList();
            if (profiles.isEmpty()) {
                Toast.makeText(c, c.getString(R.string.no_apps_in_profile), Toast.LENGTH_SHORT).show();
            } else {
                packages = profiles.split(" ");
                packagelist = new ArrayList<>();

                for (String s : packages) {
                    Log.d("Pack", s + "\n");
                    packagelist.add(s);
                }

                List<AppCount> appCounts = appCountBox.query().equal(AppCount_.profileName, profile.getProfileName())
                        .equal(AppCount_.username, username)
                        .sort(new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                return ((AppCount) o2).getCount() - ((AppCount) o1).getCount();
                            }
                        }).build().find();

                if (appCounts.size() > 5) {
                    appCounts = appCounts.subList(0, 5);
                }

                for (AppCount appCount : appCounts) {
                    Log.e("appUsageList", appCount.getPackageName());
                    for (ResolveInfo ri : allApps) {
                        if (appCount.getPackageName().equals(ri.activityInfo.packageName)) {
                            AppInfo app = new AppInfo();
                            app.setLabel(ri.loadLabel(pm));
                            app.setPackageName(ri.activityInfo.packageName);
                            app.setIcon(ri.activityInfo.loadIcon(pm));
                            appsList.add(app);
                        }
                    }
                }

                Log.e("appUsageList", "App list size:" + appsList.size());

            }

        }
    }

    @Override
    public void onBindViewHolder(AppUsageListAdapter.ViewHolder viewHolder, int i) {

        //Here we use the information in the list we created to define the views

        String appLabel = appsList.get(i).getLabel().toString();
        String appPackage = appsList.get(i).getPackageName().toString();
        Drawable appIcon = appsList.get(i).getIcon();

        TextView textView = viewHolder.labelText;
        textView.setText(appLabel);
        ImageView imageView = viewHolder.icon;
        imageView.setImageDrawable(appIcon);

        Log.e("appUsageList", "Set app data");
    }

    @Override
    public int getItemCount() {

        //This method needs to be overridden so that Androids knows how many items
        //will be making it into the list

        return appsList.size();
    }

    @Override
    public AppUsageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        int layoutRes = R.layout.app_grid_item;

        View view = inflater.inflate(layoutRes, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView labelText;
        public ImageView icon;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        public ViewHolder(View itemView) {
            super(itemView);

            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.label);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Context context = v.getContext();

            String packageName = appsList.get(pos).getPackageName().toString();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            QueryBuilder<AppCount> builder = appCountBox.query().equal(AppCount_.packageName, packageName)
                    .equal(AppCount_.profileName, profileName)
                    .equal(AppCount_.username, username);
            List<AppCount> appCounts = builder.build().find();
            AppCount appCount;
            int count = 0;
            if (appCounts.size() > 0) {
                appCount = appCounts.get(0);
                count = appCount.getCount();
            } else {
                appCount = new AppCount();
                appCount.setPackageName(packageName);
                appCount.setProfileName(profileName);
                appCount.setUsername(username);
            }
            appCount.setCount(++count);
            appCountBox.put(appCount);
            Toast.makeText(context, "Launch count: " + count, Toast.LENGTH_SHORT).show();
            context.startActivity(launchIntent);

        }
    }
}
