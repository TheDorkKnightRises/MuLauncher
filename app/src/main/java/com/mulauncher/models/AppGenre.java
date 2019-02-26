package com.mulauncher.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class AppGenre {
    @Id
    long id;
    private String appPackage;
    private String genre;

    public AppGenre(long id, String appPackage, String genre) {
        this.id = id;
        this.appPackage = appPackage;
        this.genre = genre;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
