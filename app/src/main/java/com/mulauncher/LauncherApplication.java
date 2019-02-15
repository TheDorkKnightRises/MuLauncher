package com.mulauncher;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;
import com.mulauncher.models.MyObjectBox;

import io.objectbox.BoxStore;

public class LauncherApplication extends Application {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(LauncherApplication.this).build();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}