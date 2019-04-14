package com.mulauncher.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.Profile;
import com.mulauncher.models.Profile_;
import com.mulauncher.models.User;

import java.util.List;

import io.objectbox.Box;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.user_item, viewGroup, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder viewHolder, int i) {
        String label = userList.get(i).getUsername();

        TextView textView = viewHolder.labelText;
        textView.setText(label);

        if (userList.get(i).isAdmin()) {
            viewHolder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        ImageButton deleteButton;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        ViewHolder(View itemView) {
            super(itemView);

            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.user_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    User user = userList.get(pos);
                    context.getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE).edit()
                            .remove(user.getUsername() + AppConstants.USER_LAST_PROFILE).apply();
                    Box profileBox = ((LauncherApplication) context.getApplicationContext()).getBoxStore().boxFor(Profile.class);
                    List<Profile> profiles = profileBox.query().equal(Profile_.username, user.getUsername()).build().find();
                    for (Profile p : profiles) {
                        profileBox.remove(p);
                    }
                    Box userBox = ((LauncherApplication) context.getApplicationContext()).getBoxStore().boxFor(User.class);
                    userBox.remove(user);
                    userList.remove(user);
                    UserAdapter.this.notifyDataSetChanged();
                    Toast.makeText(context, context.getString(R.string.removed_user) + " " + user.getUsername(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
