package com.mulauncher.models;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Profile implements Serializable {

    @Id
    public long Id;
    private String username;
    private String appsPackageList;
    private String profileName;

    public String getAppsPackageList() {
        return appsPackageList;
    }

    public void setAppsPackageList(String appsPackageList) {
        this.appsPackageList = appsPackageList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
    //App List, Wallpaper, Location Details to be added

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

}
