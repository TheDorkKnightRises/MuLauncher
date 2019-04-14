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
import com.mulauncher.ui.activities.CreateProfileActivity;
import com.mulauncher.ui.activities.HomeActivity;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private List<Profile> profileList;

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
        String label = profileList.get(i).getProfileName();

        TextView textView = viewHolder.labelText;
        textView.setText(label);
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView labelText;
        ImageButton editButton;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        ViewHolder(View itemView) {
            super(itemView);

            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.profile_name);
            editButton = itemView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Profile profile = profileList.get(pos);
                    if (profile != null) {
                        Intent intent = new Intent(context, CreateProfileActivity.class);
                        intent.putExtra("ProfileObject", profile);
                        context.startActivity(intent);
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Log.d("Click", Integer.toString(pos));
            String profileName = profileList.get(pos).getProfileName();
            SharedPreferences preferences = context.getSharedPreferences(AppConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
            preferences.edit().putString(preferences.getString(AppConstants.USER_NAME, "")
                    + AppConstants.USER_LAST_PROFILE, profileName).apply();
            context.startActivity(new Intent(context, HomeActivity.class));
        }
    }
}
