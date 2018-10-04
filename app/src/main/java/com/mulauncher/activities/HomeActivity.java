package com.mulauncher.activities;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.mulauncher.R;
import com.mulauncher.adapters.AppListAdapter;

public class HomeActivity extends AppCompatActivity {
    RecyclerView appListRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(HomeActivity.this, AppTourActivity.class);
        HomeActivity.this.startActivity(intent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appListRecyclerView = findViewById(R.id.appListRecyclerView);
        appListRecyclerView.setAdapter(new AppListAdapter(this, AppListAdapter.TYPE_LIST));
        appListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }
}
