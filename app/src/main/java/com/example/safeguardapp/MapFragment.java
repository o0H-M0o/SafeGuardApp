package com.example.safeguardapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import android.widget.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private DatabaseReference databaseReference;
    private DatabaseReference userContactsReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private final int FINE_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_SEND_SMS = 2;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private FloatingActionButton sendLocationBtn;
    private EmergencyContactAdapter contactAdapter;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Incidents");
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLastLocation();

        mapSearchView = view.findViewById(R.id.mapSearch);
        sendLocationBtn = view.findViewById(R.id.sendLocationBtn);
        contactAdapter = new EmergencyContactAdapter(requireContext(), new ArrayList<>());
        List<String> contactList = contactAdapter.getContactNumbers();
        sendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Send Location")
                        .setMessage("Are you sure want to send current location to your Emergency Contacts?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Proceed to send location
                                sendLocationToContacts(contactList);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing or handle cancellation
                            }
                        })
                        .show();
            }
        });



        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } else {
                        // Handle the case where no address is found
                        Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    private void sendLocationToContacts(List<String> contactList) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request Location permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
            return;
        }
        if (contactList.isEmpty()) {
            Toast.makeText(getActivity(), "No contacts available. Add emergency contacts in Profile", Toast.LENGTH_LONG).show();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Create the SMS message
                    SmsManager smsManager = SmsManager.getDefault();
                    String message = "SOS, I am in DANGER. I need help urgently. Here are my coordinates:\n" +
                            "http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();

                    // Send the SMS to all contacts
                    for (String phoneNumber : contactList) {
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    }

                    Toast.makeText(getActivity(), "Location sent to all contacts", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addHotspotMarkers() {
        // Fetch hotspots from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot hotspotSnapshot : dataSnapshot.getChildren()) {
                    String location = hotspotSnapshot.child("location").getValue(String.class);
                    if (location != null) {
                        // Convert the hotspot address to LatLng
                        LatLng hotspotLatLng = getLocationFromAddress(requireContext(), location);
                        if (hotspotLatLng != null) {
                            // Add a marker for each hotspot
                            Marker marker = myMap.addMarker(new MarkerOptions()
                                    .position(hotspotLatLng)
                                    .title("Hotspot")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.hotspot_icon)));
                            marker.setTag(location); // You can attach additional data to the marker if needed
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to fetch hotspots", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIncidentDetailsDialog(@NonNull Marker marker) {
        // Retrieve incident details from marker tag
        String location = (String) marker.getTag();
        if (location != null) {
            // Retrieve incident details from Firebase based on the location
            getIncidentDetails(location);
        }
    }

    // Retrieve incident details from Firebase based on the location
    private void getIncidentDetails(String location) {
        // Assuming you have a Firebase reference to your incidents
        DatabaseReference incidentsRef = FirebaseDatabase.getInstance().getReference("Incidents");

        // Query to get the incident details for the specified location
        incidentsRef.orderByChild("location").equalTo(location).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the result, assuming there is only one incident for a given location
                    for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
                        // Extract incident details in the correct order
                        String type = incidentSnapshot.child("type").getValue(String.class);
                        String date = incidentSnapshot.child("date").getValue(String.class);
                        String time = incidentSnapshot.child("time").getValue(String.class);
                        String photoData = incidentSnapshot.child("photoData").getValue(String.class);
                        String description = incidentSnapshot.child("description").getValue(String.class);

                        // Create an Incident object
                        Incident incident = new Incident(type, date, time, location, photoData, description);

                        // Show the incident details dialog
                        IncidentDetailsDialog.show(requireContext(), incident, location);
                    }
                } else {
                    // Handle the case where no incident is found for the location
                    Toast.makeText(requireContext(), "No incident found for the location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(requireContext(), "Failed to retrieve incident details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Convert address to LatLng
    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList == null || addressList.isEmpty()) {
                return null;
            }
            Address address = addressList.get(0);
            return new LatLng(address.getLatitude(), address.getLongitude());
        } catch (IOException e) {
            Log.e("AddressConverter", "Error converting address to LatLng", e);
            return null;
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    if (mapFragment == null) {
                        mapFragment = SupportMapFragment.newInstance();
                        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                    }
                    mapFragment.getMapAsync(MapFragment.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));

        addHotspotMarkers();

        // Set a click listener for the markers
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Show incident details dialog when a hotspot marker is clicked
                showIncidentDetailsDialog(marker);
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Log.d("MapFragment", "Permission denied: Displaying Toast");
                Toast.makeText(requireActivity(), "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
