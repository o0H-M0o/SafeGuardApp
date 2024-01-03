package com.example.safeguardapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminUpdateActivity extends AppCompatActivity {

    // Firebase
    private DatabaseReference incidentsRef;

    // Views
    private EditText detailsEditText;
    private ImageButton backButton;
    private Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update);

        // Initialize Firebase
        incidentsRef = FirebaseDatabase.getInstance().getReference("Incidents");

        // Initialize Views
        detailsEditText = findViewById(R.id.detailsEditText);
        statusSpinner = findViewById(R.id.statusSpinner);

        // Receive incident ID from the intent
        String incidentId = getIntent().getStringExtra("incidentId");

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReport(incidentId);
            }
        });

        // Set up a click listener for the Back button
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void updateReport(String incidentId) {
        // Get the selected status and details
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String details = detailsEditText.getText().toString();

        if(selectedStatus.equals("Choose a status")){
            Toast.makeText(getApplicationContext(),"Please choose a status", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(details)){
            Toast.makeText(getApplicationContext(),"Please add some details.", Toast.LENGTH_SHORT).show();
            detailsEditText.setError("Details is required");
            detailsEditText.requestFocus();
        } else {
            // Get the current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            // Update the "status" field in Firebase
            incidentsRef.child(incidentId).child("status").setValue(selectedStatus);

            // Create a new IncidentDetail object
            IncidentsDetail incidentDetail = new IncidentsDetail(details, currentTime);

            // Add the new detail to the detailsList in Firebase
            incidentsRef.child(incidentId).child("detailsList").push().setValue(incidentDetail);

            // Display a success message or perform additional actions as needed
            Toast.makeText(this, "Report updated successfully", Toast.LENGTH_SHORT).show();

            onBackPressed();
        }
    }

}
