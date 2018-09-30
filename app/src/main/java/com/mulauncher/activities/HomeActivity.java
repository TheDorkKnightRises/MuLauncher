package com.mulauncher.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mulauncher.R;
import com.mulauncher.adapters.AppListAdapter;

public class HomeActivity extends AppCompatActivity {
    RecyclerView appListRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppListAdapter(this, AppListAdapter.TYPE_GRID));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }
}
