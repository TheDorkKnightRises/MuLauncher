package com.mulauncher.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.User;
import com.mulauncher.models.User_;
import com.mulauncher.ui.fragments.AddUserDetailsFragment;

import io.objectbox.Box;

public class AddUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        final ViewGroup rootView = findViewById(R.id.rootView);

        findViewById(R.id.unlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddUserDetailsFragment addUserDetailsFragment = AddUserDetailsFragment.newInstance(AddUserActivity.this);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, addUserDetailsFragment);
                ft.commit();

                SharedPreferences preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);
                Box userBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(User.class);
                String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

                if (userBox.query().equal(User_.username, username)
                        .equal(User_.password, password)
                        .equal(User_.isAdmin, true).build().find().isEmpty()) {
                    Toast.makeText(AddUserActivity.this, getString(R.string.invalid_admin_credentials), Toast.LENGTH_SHORT).show();
                } else {
                    Scene newScene = Scene.getSceneForLayout(rootView, R.layout.layout_add_user, AddUserActivity.this);
                    Transition transition = new Fade();
                    TransitionManager.go(newScene, transition);

                    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addUserDetailsFragment.saveDetails(false);
                            Toast.makeText(AddUserActivity.this, "Added user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
