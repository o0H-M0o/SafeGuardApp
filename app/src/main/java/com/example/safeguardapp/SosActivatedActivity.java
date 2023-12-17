package com.example.safeguardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SosActivatedActivity extends AppCompatActivity implements LocationListener{

    TextView tvDate;
    TextView tvUserLocation;
    ImageButton backBtn;
    Button callBtn;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_activated);

        //set user current location
        tvUserLocation = findViewById(R.id.tv_userLoc);

            //Runtime Permission
        if(ContextCompat.checkSelfPermission(SosActivatedActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SosActivatedActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();


        //set time
        tvDate = findViewById(R.id.tv_dateTime);
        tvDate.setText(getCurrentDateTime());

        //direct call
        callBtn = findViewById(R.id.callButton);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request the permission.
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(SosActivatedActivity.this, Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(SosActivatedActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 2);
                } else {
                    // The permission is already granted. Proceed with making the call.
                    makePhoneCall();
                }

            }
        });


        //back to home
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, SosActivatedActivity.this);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();

        try{
            Geocoder geocoder = new Geocoder(SosActivatedActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            System.out.println(address);
            tvUserLocation.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private String getCurrentDateTime(){
        return new SimpleDateFormat("hh:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private void makePhoneCall(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:+60194124993"));
        startActivity(intent);
    }


    private void backToHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}