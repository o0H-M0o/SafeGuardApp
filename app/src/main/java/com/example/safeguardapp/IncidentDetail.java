package com.example.safeguardapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class IncidentDetail extends AppCompatActivity {

    //private DatabaseReference databaseReference;
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

        // Call the method to pass user data and retrieve incident details
        passUserData();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void passUserData() {
        // Get the incidentId passed from the previous activity
        String incidentId = getIntent().getStringExtra("incidentId");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Incidents");

        // Create a query to find the incident with the specified incidentId
        Query checkUserDatabase = reference.orderByChild("incidentId").equalTo(incidentId);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Iterate through all matching incidents (though there should be only one)
                    for (DataSnapshot incidentSnapshot : snapshot.getChildren()) {
                        // Retrieve incident data
                        String type = incidentSnapshot.child("type").getValue(String.class);
                        String date = incidentSnapshot.child("date").getValue(String.class);
                        String time = incidentSnapshot.child("time").getValue(String.class);
                        String location = incidentSnapshot.child("location").getValue(String.class);
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
                            Log.d("IncidentActivity", "Image URL: " + imageUrl);

                            Picasso.get()
                                    .load(imageUrl)
                                    .into(ivProof, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("IncidentActivity", "Image loaded successfully");
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            // Log error or set an error image
                                            Log.e("IncidentActivity", "Error loading image: " + e.getMessage(), e);
                                        }
                                    });
                        } else {
                            Log.e("IncidentActivity", "Image URL is null or empty");
                        }
                    }
                } else {
                    Log.e("IncidentActivity", "No matching incident found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Log.e("IncidentActivity", "Database Error: " + error.getMessage(), error.toException());
            }
        });
    }
}