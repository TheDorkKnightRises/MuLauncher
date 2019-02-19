package com.mulauncher.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mulauncher.BuildConfig;
import com.mulauncher.R;
import com.mulauncher.interfaces.AppChecklistInterface;
import com.mulauncher.models.AppInfo;
import com.mulauncher.models.SelectedAppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppChecklistAdapter extends RecyclerView.Adapter<AppChecklistAdapter.ViewHolder> {
    private List<SelectedAppInfo> appsList;
    AppChecklistInterface acInterface;

    public AppChecklistAdapter(Context c, AppChecklistInterface acInterface) {

        //This is where we build our list of app details, using the app
        //object we created to store the label, package name and icon

        PackageManager pm = c.getPackageManager();
        appsList = new ArrayList<>();
        this.acInterface = acInterface;

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);
        for (ResolveInfo ri : allApps) {
            if (ri.activityInfo.packageName.equals(BuildConfig.APPLICATION_ID))
                continue;
            AppInfo app = new AppInfo();
            app.setLabel(ri.loadLabel(pm));
            app.setPackageName(ri.activityInfo.packageName);
            app.setIcon(ri.activityInfo.loadIcon(pm));
            appsList.add(new SelectedAppInfo(app, false));
        }

        // Sort based on app name (label) ignoring case
        Collections.sort(appsList, new Comparator<SelectedAppInfo>() {
            @Override
            public int compare(SelectedAppInfo o1, SelectedAppInfo o2) {
                return o1.getAppInfo().getLabel().toString().toLowerCase()
                        .compareTo(o2.getAppInfo().getLabel().toString().toLowerCase());
            }
        });

        acInterface.setAppList(appsList);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int type;
        public TextView labelText;
        public ImageView icon;
        public CheckBox checkBox;


        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        public ViewHolder(View itemView) {
            super(itemView);
            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.label);
            icon = itemView.findViewById(R.id.icon);
            checkBox = itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            boolean selected = !checkBox.isChecked();
            checkBox.setChecked(selected);
            appsList.get(pos).setSelected(selected);
            acInterface.setAppList(appsList);
        }
    }

    @Override
    public void onBindViewHolder(final AppChecklistAdapter.ViewHolder viewHolder, final int i) {

        //Here we use the information in the list we created to define the views

        String appLabel = appsList.get(i).getAppInfo().getLabel().toString();
        String appPackage = appsList.get(i).getAppInfo().getPackageName().toString();
        Drawable appIcon = appsList.get(i).getAppInfo().getIcon();
        boolean selected = appsList.get(i).isSelected();

        TextView textView = viewHolder.labelText;
        textView.setText(appLabel);
        ImageView imageView = viewHolder.icon;
        imageView.setImageDrawable(appIcon);
        CheckBox checkBox = viewHolder.checkBox;
        checkBox.setChecked(selected);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appsList.get(viewHolder.getAdapterPosition()).setSelected(isChecked);
            }
        });
    }


    @Override
    public int getItemCount() {

        //This method needs to be overridden so that Androids knows how many items
        //will be making it into the list

        return appsList.size();
    }


    @Override
    public AppChecklistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //This is what adds the code we've written in here to our target view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.app_checklist_item, parent, false);

        return new AppChecklistAdapter.ViewHolder(view);
    }
}

