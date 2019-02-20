package com.mulauncher.ui.activities;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.view.FadeableViewPager;
import com.mulauncher.R;
import com.mulauncher.ui.fragments.AddUserDetailsFragment;

public class AppTourActivity extends IntroActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);

        super.onCreate(savedInstanceState);

        final AddUserDetailsFragment addUserDetailsFragment = AddUserDetailsFragment.newInstance(this);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.Launcher_Title)
                .image(R.drawable.intro_image)
                .background(R.color.background)
                .backgroundDark(R.color.backgroundDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .image(R.drawable.personalization)
                .title(R.string.title_1)
                .description(R.string.description_1)
                .background(R.color.background)
                .backgroundDark(R.color.backgroundDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .image(R.drawable.adapt)
                .title(R.string.title_2)
                .description(R.string.description_2)
                .background(R.color.background)
                .backgroundDark(R.color.backgroundDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .image(R.drawable.quick_access)
                .title(R.string.title_3)
                .description(R.string.description_3)
                .background(R.color.background)
                .backgroundDark(R.color.backgroundDark)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.add_user)
                .description(R.string.add_user_desc)
                .background(R.color.background)
                .backgroundDark(R.color.backgroundDark)
                .scrollable(false)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.background)
                .backgroundDark(R.color.backgroundDark)
                .fragment(addUserDetailsFragment)
                .build());

        autoplay(5000, INFINITE);
        setPageScrollDuration(500);

        addOnPageChangeListener(new FadeableViewPager.OnOverscrollPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (i == 5) {
                    addUserDetailsFragment.saveDetails(true);
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

}
