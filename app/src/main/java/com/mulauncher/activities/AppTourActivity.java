package com.mulauncher.activities;

import android.content.Intent;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.mulauncher.R;

public class AppTourActivity extends IntroActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);

        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.Launcher_Title)
                .image(R.drawable.intro_image)
                .background(R.color.background_1)
                .backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .image(R.drawable.personalization)
                .title(R.string.title_1)
                .description(R.string.description_1)
                .background(R.color.background_1)
                .backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .image(R.drawable.adapt)
                .title(R.string.title_2)
                .description(R.string.description_2)
                //.image(R.drawable.image_1)
                .background(R.color.background_1)
                .backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .image(R.drawable.quick_access)
                .title(R.string.title_3)
                .description(R.string.description_3)
                //.image(R.drawable.image_1)
                .background(R.color.background_1)
                .backgroundDark(R.color.background_dark_1)
                .scrollable(false)
                .build());


        autoplay(2000, INFINITE);
        setPageScrollDuration(1000);

    }
}
