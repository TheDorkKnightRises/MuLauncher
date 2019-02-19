package com.mulauncher.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mulauncher.AppConstants;
import com.mulauncher.R;
import com.mulauncher.models.Profile;
import com.mulauncher.ui.activities.HomeActivity;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    Context context;
    List<Profile> profileList;

    public ProfileAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.profile_item, viewGroup, false);

        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder viewHolder, int i) {
        String appLabel = profileList.get(i).getProfileName();

        TextView textView = viewHolder.labelText;
        textView.setText(appLabel);
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView labelText;
        public ImageButton icon;
        int type;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        public ViewHolder(View itemView) {
            super(itemView);

            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.profile_name);
            icon = itemView.findViewById(R.id.edit_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Log.d("Click", Integer.toString(pos));
            // TODO: Switch profile here
            String profileName = profileList.get(pos).getProfileName();
            SharedPreferences preferences = context.getSharedPreferences(AppConstants.PROFILE, Context.MODE_PRIVATE);
            preferences.edit().putString(AppConstants.PROFILE, profileName).apply();
            context.startActivity(new Intent(context, HomeActivity.class));
        }
    }
}
