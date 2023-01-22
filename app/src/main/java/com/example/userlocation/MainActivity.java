package com.example.userlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity<REQUEST_CODE> extends AppCompatActivity {
private TextView textViewLatitude,textViewLongitude;
private LocationRequest request;
private LocationSettingsRequest.Builder builder;
private final int REQUEST_CODE=2728;
private LocationCallback locationCallback;
private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLatitude=findViewById(R.id.textViewLatitude);
        textViewLongitude=findViewById(R.id.textViewLongitude);
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        request=new LocationRequest()
                .setFastestInterval(300)
                .setInterval(300)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // we have created location request oject

        builder=new LocationSettingsRequest.Builder().addLocationRequest(request);  // creating a builder

        //creating task object
        Task<LocationSettingsResponse> result=LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException)
                {
                    try {
                        ResolvableApiException resolvable=(ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });

        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult==null)
                {
                    textViewLongitude.setText("null");
                    return;
                }
                for(Location location : locationResult.getLocations())
                {
                    textViewLatitude.setText(String.valueOf(location.getLatitude()));
                    textViewLongitude.setText(String.valueOf(location.getLongitude()));
                }

            }
        };

    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdates()
    {
        if(fusedLocationProviderClient!=null)
        {
            fusedLocationProviderClient.requestLocationUpdates(request,locationCallback,getMainLooper());
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}