package com.mulauncher.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Profile {

    @Id
    public long Id;
    private String username;

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

    private String profileName;
    //App List, Wallpaper, Location Details to be added

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

}
