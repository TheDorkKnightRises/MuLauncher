package com.mulauncher.ui.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.interfaces.AppGenreChecklistInterface;
import com.mulauncher.models.AppGenre;
import com.mulauncher.models.AppGenre_;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

public class AppGenreAdapter extends RecyclerView.Adapter<AppGenreAdapter.ViewHolder> {

    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String ERROR = "error";
    AppGenreChecklistInterface appGenreChecklistInterface;
    Box genreBox;
    private List<AppGenre> genre;
    private Map<String, Integer> map;

    public AppGenreAdapter(Context context, AppGenreChecklistInterface appGenreChecklistInterface) {

        this.appGenreChecklistInterface = appGenreChecklistInterface;
        genreBox = ((LauncherApplication) context.getApplicationContext()).getBoxStore().boxFor(AppGenre.class);

        FetchCategoryTask fetchCategoryTask = new FetchCategoryTask(context);
        fetchCategoryTask.execute();

        genre = genreBox.getAll();
        for (AppGenre a : genre)
            map.put(a.getGenre(), 0);
        appGenreChecklistInterface.setAppGenreList(map);
    }

    @Override
    public void onBindViewHolder(final AppGenreAdapter.ViewHolder viewHolder, final int i) {

        //Here we use the information in the list we created to define the views
        String appLabel = " ";
        Boolean selected = false;
        int count = 0;

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (i == count) {
                appLabel = e.getKey();
                selected = e.getValue() == 1;
                break;
            }
            count++;
        }

        TextView textView = viewHolder.labelText;
        textView.setText(appLabel);
        CheckBox checkBox = viewHolder.checkBox;
        checkBox.setChecked(selected);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = viewHolder.getAdapterPosition(), count = 0;
                for (Map.Entry<String, Integer> e : map.entrySet()) {
                    if (pos == count) {
                        e.setValue(isChecked == true ? 1 : 0);
                        break;
                    }
                    count++;
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        //This method needs to be overridden so that Androids knows how many items
        //will be making it into the list

        return map.size();
    }

    @Override
    public AppGenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //This is what adds the code we've written in here to our target view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.genre_checklist_item, parent, false);

        return new AppGenreAdapter.ViewHolder(view);
    }

    private class FetchCategoryTask extends AsyncTask<Void, Void, Void> {

        private final String TAG = FetchCategoryTask.class.getSimpleName();
        private PackageManager pm;
        private Context c;

        public FetchCategoryTask(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... errors) {
            String category;
            pm = c.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            Iterator<ApplicationInfo> iterator = packages.iterator();
            while (iterator.hasNext()) {
                ApplicationInfo packageInfo = iterator.next();

                if (genreBox.query().equal(AppGenre_.genre, packageInfo.packageName).build().findFirst() == null) {
                    String query_url = GOOGLE_URL + packageInfo.packageName;
                    Log.i(TAG, query_url);
                    category = getCategory(query_url);
                    genreBox.put(new AppGenre(0, packageInfo.packageName, category));
                }
            }
            return null;
        }


        private String getCategory(String query_url) {

            try {
                Document doc = Jsoup.connect(query_url).get();
                Element link = doc.select("span[itemprop=genre]").first();
                return link.text();
            } catch (Exception e) {
                return ERROR;
            }

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView labelText;
        public CheckBox checkBox;
        int count = 0;


        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each row
        public ViewHolder(View itemView) {
            super(itemView);
            //Finds the views from our row.xml
            labelText = itemView.findViewById(R.id.label);
            checkBox = itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            count = 0;
            int pos = getAdapterPosition();
            boolean selected = !checkBox.isChecked();
            checkBox.setChecked(selected);
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                if (pos == count) {
                    e.setValue(1);
                    break;
                }
                count++;
            }

            appGenreChecklistInterface.setAppGenreList(map);
        }
    }
}
