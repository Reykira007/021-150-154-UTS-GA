package com.uts.gis6a;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PointActivity extends AppCompatActivity implements PointAddress.OnTaskCompleted {

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "dapatkan_lokasi";

    // Views
    private Button mLocationButton;
    private TextView mLocationTextView;
    private ImageView mAndroidImageView;

    //Location classes
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    EditText et_latitude, et_longitude, nama_lengkap, no_whatsapp;
    TextView lokasi;

    LottieAnimationView api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        //Point Variable
        mLocationButton = (Button) findViewById(R.id.btn_location);
        lokasi = (TextView) findViewById(R.id.lokasi);
        et_latitude = (EditText) findViewById(R.id.et_latitude);
        et_longitude = (EditText) findViewById(R.id.et_longitude);
        nama_lengkap = (EditText) findViewById(R.id.nama_lengkap);
        no_whatsapp = (EditText) findViewById(R.id.no_whatsapp);
        api = (LottieAnimationView) findViewById(R.id.fire);

        // Initialize the FusedLocationClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PointActivity.this);

        // Restore the state if the activity is recreated.
        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
        }

        //Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.notifications);

        //Pindah menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext()
                                , SearchActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                , MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.notifications:
                        return true;
                }
                return false;
            }
        });

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nama_lengkap.getText().toString().isEmpty()) {
                    Toast.makeText(PointActivity.this, "Masukkan Nama Anda", Toast.LENGTH_SHORT).show();
                } else if (no_whatsapp.getText().toString().isEmpty()) {
                    Toast.makeText(PointActivity.this, "Masukkan No. WhatsApp", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mTrackingLocation) {
                        startTrackLocation();
                    } else {
                        stopTrackingLocation();
                    }
                }
            }
        });

        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mTrackingLocation) {
                    new PointAddress(PointActivity.this, PointActivity.this).execute(locationResult.getLastLocation());
                }
            }
        };
        return;
    }

    private void startTrackLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (!(location == null)) {
                        et_latitude.setText(String.valueOf(location.getLatitude()));
                        et_latitude.setEnabled(false);
                        et_longitude.setText(String.valueOf(location.getLongitude()));
                        et_longitude.setEnabled(false);
                        nama_lengkap.setEnabled(false);
                        no_whatsapp.setEnabled(false);
                        api.playAnimation();
                    } else {
                        Toast.makeText(PointActivity.this, "Aktifkan Lokasi", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PointActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);
            mLocationButton.setText("STOP");
        }
    }

    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mLocationButton.setText("DAPATKAN LOKASI");
            lokasi.setText(R.string.lokasi);
            nama_lengkap.setEnabled(true);
            nama_lengkap.setText("");
            no_whatsapp.setText("");
            no_whatsapp.setEnabled(true);
            et_latitude.setEnabled(true);
            et_latitude.setText("");
            et_longitude.setEnabled(true);
            et_longitude.setText("");
            api.pauseAnimation();
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:

                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startTrackLocation();
                } else {
                    Toast.makeText(this,
                            R.string.izin_ditolak,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        if (mTrackingLocation) {
            lokasi.setText(result);
        }
    }

    @Override
    public void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mTrackingLocation) {
            startTrackLocation();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mTrackingLocation) {
            stopTrackingLocation();
        }
        super.onDestroy();
    }
};