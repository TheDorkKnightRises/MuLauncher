package com.mulauncher.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.User;
import com.mulauncher.ui.adapters.UserAdapter;

import java.util.List;

import io.objectbox.Box;

public class ManageUsersActivity extends AppCompatActivity {

    RecyclerView usersRecyclerView;
    Box userBox;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        userBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(User.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        userList = userBox.getAll();
        Log.d(getLocalClassName(), "Users: " + userList.size());
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setAdapter(new UserAdapter(this, userList));
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
