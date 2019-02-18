package com.mulauncher.models;

import java.util.ArrayList;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Profile {

    @Id
    public long Id;
    public ToOne<User> user;

    public ArrayList<AppInfo> getApplist() {
        return applist;
    }

    public void setApplist(ArrayList<AppInfo> applist) {
        this.applist = applist;
    }

    public ArrayList<AppInfo> applist;

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(ToOne<User> user) {
        this.user = user;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    private String profileName;
    //App List, Wallpaper, Location Details to be added

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

}
