package com.example.safeguardapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseIncidentActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_incident);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Incidents");
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);

        // Set click listener for the Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button press
                Intent intent = new Intent(ChooseIncidentActivity.this, MainActivityForTracking.class);
                startActivity(intent);
                finish();  // This will finish the current activity and prevent going back to ChooseIncidentActivity
            }
        });

        setupRecyclerView();
        getIncidents();
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getIncidents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Incidents> incidentList = new ArrayList<>();

                    for (DataSnapshot incidentSnapshot : snapshot.getChildren()) {
                        String incidentId = incidentSnapshot.getKey();
                        String date = incidentSnapshot.child("date").getValue(String.class);
                        String time = incidentSnapshot.child("time").getValue(String.class);
                        String status = incidentSnapshot.child("status").getValue(String.class);
                        String photoData = incidentSnapshot.child("photoData").getValue(String.class);
                        String type = incidentSnapshot.child("type").getValue(String.class);

                        List<IncidentsDetail> detailsList = new ArrayList<>();
                        for (DataSnapshot detailSnapshot : incidentSnapshot.child("detailsList").getChildren()) {
                            String details = detailSnapshot.child("details").getValue(String.class);
                            String updateTime = detailSnapshot.child("updateTime").getValue(String.class);
                            detailsList.add(new IncidentsDetail(details, updateTime));
                        }

                        incidentList.add(new Incidents(incidentId, date, time, status, type, photoData, detailsList));
                    }

                    CustomAdapter adapter = new CustomAdapter(ChooseIncidentActivity.this, incidentList);
                    recyclerView.setAdapter(adapter);

                    // Set item click listener to open AdminUpdateActivity with selected incident details
                    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Incidents selectedIncident = incidentList.get(position);

                            Intent intent = new Intent(ChooseIncidentActivity.this, AdminUpdateActivity.class);
                            intent.putExtra("incidentId", selectedIncident.getIncidentId());
                            startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // Handle long item click if needed
                        }
                    }));

                } else {
                    Toast.makeText(ChooseIncidentActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChooseIncidentActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button press
                finish();  // Finish the current activity (ChooseIncidentActivity)
            }
        });
    }
}





