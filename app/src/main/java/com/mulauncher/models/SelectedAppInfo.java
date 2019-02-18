package com.mulauncher.models;

public class SelectedAppInfo {
    AppInfo appInfo;
    boolean selected;

    public SelectedAppInfo(AppInfo appInfo, boolean selected) {
        this.appInfo = appInfo;
        this.selected = selected;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
