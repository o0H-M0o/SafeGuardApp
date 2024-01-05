package com.example.safeguardapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SafetyAlertNotification extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {


    TextView textview;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapFragment mapFragment;
    private DatabaseReference databaseReference;
    private GeofencingClient geofencingClient;
    private GoogleApiClient googleApiClient;
    private BroadcastReceiver geofenceBroadcastReceiver;

    // List of dangerous locations


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textview=findViewById(R.id.TVAlertNoti);
        String data= getIntent().getStringExtra("data");
        textview.setText(data);
    }
}
