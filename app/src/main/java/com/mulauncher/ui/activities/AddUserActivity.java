package com.mulauncher.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private static final int REQUEST_PROFILE_CREATION = 123;
    AddUserDetailsFragment addUserDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        final ViewGroup rootView = findViewById(R.id.rootView);

        findViewById(R.id.unlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Box userBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(User.class);
                final String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                Log.d("AddUser", "Test 1");

                if (userBox.query().equal(User_.username, username)
                        .equal(User_.password, password)
                        .equal(User_.isAdmin, true).build().find().isEmpty()) {

                    Log.d("AddUser", "Test 2");

                    Toast.makeText(AddUserActivity.this, getString(R.string.invalid_admin_credentials), Toast.LENGTH_SHORT).show();
                } else {

                    Scene newScene = Scene.getSceneForLayout(rootView, R.layout.layout_add_user, AddUserActivity.this);
                    Transition transition = new Fade();
                    TransitionManager.go(newScene, transition);


                    Log.d("AddUser", "Test 3");
                    addUserDetailsFragment = AddUserDetailsFragment.newInstance(AddUserActivity.this);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment, addUserDetailsFragment);
                    ft.commit();

                    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(AddUserActivity.this, CreateProfileActivity.class);
                            String username = ((EditText) findViewById(R.id.name_edittext)).getText().toString();
                            i.putExtra(AppConstants.USER_NAME, username);
                            startActivityForResult(i, REQUEST_PROFILE_CREATION);

                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PROFILE_CREATION && resultCode == Activity.RESULT_OK) {
            addUserDetailsFragment.saveDetails(false);
            Toast.makeText(AddUserActivity.this, "Added user", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
