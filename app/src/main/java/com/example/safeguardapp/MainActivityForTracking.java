package com.example.safeguardapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
public class MainActivityForTracking extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Incidents");
        recyclerView = findViewById(R.id.recyclerView);

        // Set up the button click listener for choosing an incident
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open ChooseIncidentActivity to choose the incident to update
                Intent intent = new Intent(MainActivityForTracking.this, ChooseIncidentActivity.class);
                startActivity(intent);
            }
        });

        // Set up the backButton click listener
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setupRecyclerView();
        getdata();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        // This will be called when the back button is pressed
        super.onBackPressed();
    }

    private void getdata() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Incidents> incidentList = new ArrayList<>();

                    for (DataSnapshot incidentSnapshot : snapshot.getChildren()) {
                        String incidentId = incidentSnapshot.getKey(); // Get the unique incident ID
                        String date = incidentSnapshot.child("date").getValue(String.class);
                        String time = incidentSnapshot.child("time").getValue(String.class);
                        String status = incidentSnapshot.child("status").getValue(String.class);

                        List<IncidentsDetail> detailsList = new ArrayList<>();
                        for (DataSnapshot detailSnapshot : incidentSnapshot.child("detailsList").getChildren()) {
                            String details = detailSnapshot.child("details").getValue(String.class);
                            String updateTime = detailSnapshot.child("updateTime").getValue(String.class);
                            detailsList.add(new IncidentsDetail(details, updateTime));
                        }

                        incidentList.add(new Incidents(incidentId, date, time, status, detailsList));
                    }

                    adapter.setIncidentList(incidentList);
                } else {
                    Toast.makeText(MainActivityForTracking.this, "No data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivityForTracking.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


