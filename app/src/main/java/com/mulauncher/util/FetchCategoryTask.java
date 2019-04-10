package com.mulauncher.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.mulauncher.LauncherApplication;
import com.mulauncher.interfaces.OnGenreFetchListener;
import com.mulauncher.models.AppGenre;
import com.mulauncher.models.AppGenre_;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Iterator;
import java.util.List;

import io.objectbox.Box;

public class FetchCategoryTask extends AsyncTask<Void, Void, Void> {

    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    private final String TAG = FetchCategoryTask.class.getSimpleName();
    Box genreBox;
    OnGenreFetchListener onGenreFetchListener;
    private PackageManager pm;
    private Context c;
    private String genre;


    public FetchCategoryTask(Context c, OnGenreFetchListener onGenreFetchListener) {
        this.c = c;
        this.onGenreFetchListener = onGenreFetchListener;
    }

    @Override
    protected Void doInBackground(Void... errors) {
        String category;
        pm = c.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Iterator<ApplicationInfo> iterator = packages.iterator();
        genreBox = ((LauncherApplication) c.getApplicationContext()).getBoxStore().boxFor(AppGenre.class);

        while (iterator.hasNext()) {
            ApplicationInfo packageInfo = iterator.next();

            if (genreBox.query().equal(AppGenre_.appPackage, packageInfo.packageName).build().findFirst() == null) {
                String query_url = GOOGLE_URL + packageInfo.packageName;
                Log.i(TAG, query_url);
                category = getCategory(query_url);
                genreBox.put(new AppGenre(0, packageInfo.packageName, category));
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (onGenreFetchListener != null)
            onGenreFetchListener.onGenreFetch();
    }

    private String getCategory(String query_url) {

        try {
            //Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad").get();
            Document doc = Jsoup.connect(query_url).get();
            genre = doc.getElementsByAttributeValue("itemprop", "genre").text();
            Log.d("Genre", genre);
            return genre.trim().equals("") ? "Unknown" : genre;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }

    }
}