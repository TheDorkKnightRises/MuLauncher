package com.mulauncher.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mulauncher.AppConstants;
import com.mulauncher.LauncherApplication;
import com.mulauncher.R;
import com.mulauncher.models.Profile;
import com.mulauncher.models.Profile_;

import io.objectbox.Box;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 35;
    private MapView mapView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationComponent locationComponent;

    Profile profile;
    Box profileBox;
    SharedPreferences user_preferences;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profile = (Profile) getIntent().getSerializableExtra("ProfileObject");
        profileBox = ((LauncherApplication) getApplicationContext()).getBoxStore().boxFor(Profile.class);

        user_preferences = getSharedPreferences(AppConstants.USER_PREFERENCES, MODE_PRIVATE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_location);

        View mAddGeofencesButton = findViewById(R.id.add_geofence);
        mAddGeofencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissions()) {
                    requestPermissions();
                    return;
                }
                if (location != null) {
                    // TODO: Check for overlap in geofences
                    profile.setLatitude(location.getLatitude());
                    profile.setLongitude(location.getLongitude());
                } else
                    Toast.makeText(LocationActivity.this, "Waiting for location", Toast.LENGTH_SHORT).show();

                Profile existingProfile = (Profile) profileBox.query()
                        .equal(Profile_.profileName, profile.getProfileName())
                        .equal(Profile_.username, profile.getUsername()).build().findFirst();
                if (existingProfile != null) {
                    // TODO: Maybe we could warn user before overwriting their existing profile
                    profileBox.remove(existingProfile);
                }
                profileBox.put(profile);
                user_preferences.edit().putString(profile.getUsername() + AppConstants.USER_LAST_PROFILE, profile.getProfileName()).apply();
                finish();

            }
        });

        findViewById(R.id.skip_geofence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile existingProfile = (Profile) profileBox.query()
                        .equal(Profile_.profileName, profile.getProfileName())
                        .equal(Profile_.username, profile.getUsername()).build().findFirst();
                if (existingProfile != null) {
                    // TODO: Maybe we could warn user before overwriting their existing profile
                    profileBox.remove(existingProfile);
                }
                profileBox.put(profile);
                user_preferences.edit().putString(profile.getUsername() + AppConstants.USER_LAST_PROFILE, profile.getProfileName()).apply();
                finish();
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    protected void createLocationRequest() {
        final LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult == null) {
                                    return;
                                }
                                for (Location location : locationResult.getLocations()) {
                                    LocationActivity.this.location = location;
                                    //populateGeofenceList(location);
                                    if (locationComponent != null)
                                        locationComponent.forceLocationUpdate(location);
                                }
                            }
                        },
                        null /* Looper */);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                } else {
                    // Get an instance of the component
                    locationComponent = mapboxMap.getLocationComponent();

                    // Activate with options
                    locationComponent.activateLocationComponent(LocationActivity.this, mapboxMap.getStyle());

                    // Enable to make component visible
                    locationComponent.setLocationComponentEnabled(true);

                    // Set the component's render mode
                    locationComponent.setRenderMode(RenderMode.COMPASS);

                    // Set the component's camera mode
                    locationComponent.setCameraMode(CameraMode.TRACKING);

                    // Create a Location Request from Google Maps
                    createLocationRequest();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            // TODO: Implement later
            // performPendingGeofenceTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar(R.string.location_permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(LocationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(LocationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            createLocationRequest();
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

}
