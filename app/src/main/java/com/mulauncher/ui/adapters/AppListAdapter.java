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

import com.mulauncher.AppConstants;
import com.mulauncher.BuildConfig;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.AppInfo;
import com.mulauncher.models.Profile;
import com.mulauncher.models.Profile_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.objectbox.Box;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    public static final int TYPE_LIST = 0;
    public static final int TYPE_GRID = 1;
    private List<AppInfo> appsList;
    private int type;

    public AppListAdapter(Context c, int type) {

        // Set type (list or grid)
        this.type = type;

        //This is where we build our list of app details, using the app
        //object we created to store the label, package name and icon

        PackageManager pm = c.getPackageManager();
        Box profileBox;
        Profile profile;
        String[] packages;
        List<String> packagelist;
        List<Profile> proList;
        SharedPreferences preferences = c.getSharedPreferences(AppConstants.PROFILE, Context.MODE_PRIVATE);
        //Need to know more about SharedPreferences .....on using USER_NAME instead of USER_PREFERENCE..NULL Pointer Exception
        SharedPreferences userpref = c.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);

        appsList = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);

        if (preferences.getString(AppConstants.PROFILE, "abc").equals("abc")) {
            for (ResolveInfo ri : allApps) {
                if (ri.activityInfo.packageName.equals(BuildConfig.APPLICATION_ID))
                    continue;
                AppInfo app = new AppInfo();
                app.setLabel(ri.loadLabel(pm));
                app.setPackageName(ri.activityInfo.packageName);
                app.setIcon(ri.activityInfo.loadIcon(pm));
                appsList.add(app);
            }

            // Sort based on app name (label) ignoring case
            Collections.sort(appsList, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo o1, AppInfo o2) {
                    return o1.getLabel().toString().toLowerCase().compareTo(o2.getLabel().toString().toLowerCase());
                }
            });
        } else {
            profileBox = ((LauncherApplication) c.getApplicationContext()).getBoxStore().boxFor(Profile.class);

            proList = profileBox.getAll();
            for (Profile p : proList)
                Log.d("AllProfiles", p.getProfileName() + " " + p.getUsername() + " " + p.getAppsPackageList());

            profile = (Profile) profileBox.query().equal(Profile_.profileName, preferences.getString(AppConstants.PROFILE, ""))
                    .equal(Profile_.username, userpref.getString(AppConstants.USER_NAME, ""))
                    .build().findFirst();


            packages = profile.getAppsPackageList().split(" ");
            packagelist = new ArrayList<>();

            for (String s : packages) {
                Log.d("Pack", s + "\n");
                packagelist.add(s);
            }

            for (ResolveInfo ri : allApps) {
                if (ri.activityInfo.packageName.equals(BuildConfig.APPLICATION_ID))
                    continue;
                if (packagelist.contains(ri.activityInfo.packageName)) {
                    AppInfo app = new AppInfo();
                    app.setLabel(ri.loadLabel(pm));
                    app.setPackageName(ri.activityInfo.packageName);
                    app.setIcon(ri.activityInfo.loadIcon(pm));
                    appsList.add(app);
                }
            }

            // Sort based on app name (label) ignoring case
            Collections.sort(appsList, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo o1, AppInfo o2) {
                    return o1.getLabel().toString().toLowerCase().compareTo(o2.getLabel().toString().toLowerCase());
                }
            });

        }
    }

    @Override
    public void onBindViewHolder(AppListAdapter.ViewHolder viewHolder, int i) {

        //Here we use the information in the list we created to define the views

        String appLabel = appsList.get(i).getLabel().toString();
        String appPackage = appsList.get(i).getPackageName().toString();
        Drawable appIcon = appsList.get(i).getIcon();

        TextView textView = viewHolder.labelText;
        textView.setText(appLabel);
        ImageView imageView = viewHolder.icon;
        imageView.setImageDrawable(appIcon);
    }

    @Override
    public int getItemCount() {

        //This method needs to be overridden so that Androids knows how many items
        //will be making it into the list

        return appsList.size();
    }

    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        int layoutRes = (type == TYPE_LIST) ? R.layout.app_list_item : R.layout.app_grid_item;

        View view = inflater.inflate(layoutRes, parent, false);

        return new ViewHolder(view, type);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView labelText;
        public ImageView icon;
        int type;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        public ViewHolder(View itemView, int type) {
            super(itemView);
            this.type = type;

            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.label);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Context context = v.getContext();

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appsList.get(pos).getPackageName().toString());
            context.startActivity(launchIntent);

        }
    }
}
