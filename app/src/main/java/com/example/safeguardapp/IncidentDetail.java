package com.example.safeguardapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class IncidentDetail extends AppCompatActivity {

    private TextView tvType, tvDate, tvTime, tvLocation, tvDescription;
    private ImageView ivProof, ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_detail);

        // Initialize views
        tvType = findViewById(R.id.TVType);
        tvDate = findViewById(R.id.TVDate);
        tvTime = findViewById(R.id.TVTime);
        tvLocation = findViewById(R.id.TVLocation);
        tvDescription = findViewById(R.id.TVDes);
        ivProof = findViewById(R.id.IVProof);
        ivBack = findViewById(R.id.IVBack);

        // Call the method to retrieve incident details based on location
        retrieveIncidentDetails();

        // Set up a click listener for the back button
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity and go back
                finish();
            }
        });
    }

    private void retrieveIncidentDetails() {
        // Get the location passed from the previous activity
        String location = getIntent().getStringExtra("location");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Incidents");

        // Create a query to find the incident with the specified location
        Query checkLocation = reference.orderByChild("location").equalTo(location);

        checkLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Iterate through all matching incidents (though there should be only one)
                    for (DataSnapshot incidentSnapshot : snapshot.getChildren()) {
                        // Retrieve incident data
                        String type = incidentSnapshot.child("type").getValue(String.class);
                        String date = incidentSnapshot.child("date").getValue(String.class);
                        String time = incidentSnapshot.child("time").getValue(String.class);
                        String imageUrl = incidentSnapshot.child("photoData").getValue(String.class); // Retrieve image URL
                        String description = incidentSnapshot.child("description").getValue(String.class);

                        // Update UI with incident details
                        tvType.setText(type);
                        tvDate.setText(date);
                        tvTime.setText(time);
                        tvLocation.setText(location);
                        tvDescription.setText(description);

                        // Load the image using Picasso or any other image-loading library
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(ivProof);
                        }
                    }
                } else {
                    // Handle the case where no matching incident is found for the location
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}