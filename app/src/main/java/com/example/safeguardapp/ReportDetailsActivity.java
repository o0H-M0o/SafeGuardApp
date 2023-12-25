package com.example.safeguardapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ReportDetailsActivity extends AppCompatActivity {

    private TextView tvType, tvDate, tvTime, tvLocation, tvDetails, tvUpdateTime;

    private CardView cardViewIncidentInfo, cardViewStatusDetails;
    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        tvType = findViewById(R.id.Type);
        tvDate = findViewById(R.id.Date);
        tvTime = findViewById(R.id.Time);
        tvLocation = findViewById(R.id.Location);
        tvDetails = findViewById(R.id.Details);
        cardViewIncidentInfo = findViewById(R.id.cardViewIncidentInfo);
        cardViewStatusDetails = findViewById(R.id.cardViewStatusDetails);
        ivBack = findViewById(R.id.IVBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String incidentId = getIntent().getStringExtra("incidentId");

        retrieveIncidentDetails(incidentId);
    }
    private void retrieveIncidentDetails(String incidentId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Incidents").child(incidentId);

        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    String type = snapshot.child("type").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    StringBuilder detailsBuilder = new StringBuilder(); // Use StringBuilder for efficient string concatenation

                    if (snapshot.child("detailsList").exists()) {
                        boolean isFirstEntry = true;
                        for (DataSnapshot detailsSnapshot : snapshot.child("detailsList").getChildren()) {
                            if (isFirstEntry) {
                                isFirstEntry = false;
                                continue;
                            }

                            detailsBuilder.append("Update Time: ").append(detailsSnapshot.child("updateTime").getValue(String.class))
                                    .append("\nStatus: ").append(detailsSnapshot.child("status").getValue(String.class))
                                    .append("\nDetails: ").append(detailsSnapshot.child("details").getValue(String.class)).append("\n\n");
                        }
                    }

                    tvType.setText("Type: " + type);
                    tvDate.setText("Date: " + date);
                    tvTime.setText("Time: " + time);
                    tvLocation.setText("Location: " + location);
                    tvDetails.setText(detailsBuilder.toString().trim());

                    if (location != null && !location.isEmpty()) {
                        cardViewIncidentInfo.setVisibility(View.VISIBLE);
                    } else {
                        cardViewIncidentInfo.setVisibility(View.GONE);
                    }

                    if (status != null && !status.isEmpty() && detailsBuilder.length() > 0) {
                        cardViewStatusDetails.setVisibility(View.VISIBLE);
                    } else {
                        cardViewStatusDetails.setVisibility(View.GONE);
                    }

                }
            }
        });
    }
}