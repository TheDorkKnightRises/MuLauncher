package com.mulauncher.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class AppCount {
    @Id
    long id;
    int count;
    String packageName;
    private String username;
    private String profileName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
