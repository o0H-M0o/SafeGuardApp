package com.example.safeguardapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;  // Add this import

public class LocEduFragment extends Fragment {

    FloatingActionButton fab;
    DatabaseReference databaseReferenceLoc, databaseReferenceEdu, userRef;
    ValueEventListener eventListenerLoc, eventListenerEdu;
    RecyclerView horizontalRecyclerViewLocalOrg, verticalRecyclerViewEdu;
    List<DataClass> dataListLocalOrg, dataListEdu;
    MyAdapter adapterEdu;
    LocalOrgUpperPartAdapter adapterLocalOrg;
    TextView localOrganizationTextView, educationalResourcesTextView;
    ImageView localOrganizationSearchImageView, educationalResourcesSearchImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loc_edu, container, false);

        horizontalRecyclerViewLocalOrg = rootView.findViewById(R.id.horizontalRecyclerView);
        verticalRecyclerViewEdu = rootView.findViewById(R.id.verticalRecyclerView);
        fab = rootView.findViewById(R.id.fab);
        localOrganizationTextView = rootView.findViewById(R.id.localOrganizationTextView);
        educationalResourcesTextView = rootView.findViewById(R.id.educationalResourcesTextView);
        localOrganizationSearchImageView = rootView.findViewById(R.id.localOrganizationSearchImageView);
        educationalResourcesSearchImageView = rootView.findViewById(R.id.educationalResourcesSearchImageView);

        fab.setVisibility(View.GONE);

        GridLayoutManager horizontalLayoutManagerLocalOrg = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerViewLocalOrg.setLayoutManager(horizontalLayoutManagerLocalOrg);

        LinearLayoutManager verticalLayoutManagerEdu = new LinearLayoutManager(getContext());
        verticalRecyclerViewEdu.setLayoutManager(verticalLayoutManagerEdu);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataListLocalOrg = new ArrayList<>();
        dataListEdu = new ArrayList<>();

        adapterLocalOrg = new LocalOrgUpperPartAdapter(getContext(), dataListLocalOrg);
        adapterEdu = new MyAdapter(getContext(), dataListEdu);

        horizontalRecyclerViewLocalOrg.setAdapter(adapterLocalOrg);
        verticalRecyclerViewEdu.setAdapter(adapterEdu);

        databaseReferenceLoc = FirebaseDatabase.getInstance().getReference("Local Org ");
        databaseReferenceEdu = FirebaseDatabase.getInstance().getReference("Educational ");
        dialog.show();
        eventListenerLoc = databaseReferenceLoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataListLocalOrg.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataListLocalOrg.add(dataClass);
                }
                adapterLocalOrg.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        eventListenerEdu = databaseReferenceEdu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataListEdu.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataListEdu.add(dataClass);
                }
                adapterEdu.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String role = dataSnapshot.child("role").getValue(String.class);

                        // Show/hide views based on the user's role
                        switch (role) {
                            case "Admin":
                                fab.setVisibility(View.VISIBLE);
                                break;

                            default:
                                break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error if any
                }
            });
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUploadActivity();
            }
        });

        localOrganizationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocalOrganizationActivity();
            }
        });

        localOrganizationSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocalOrganizationActivity();
            }
        });

        educationalResourcesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEducationalResourcesActivity();
            }
        });

        educationalResourcesSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEducationalResourcesActivity();
            }
        });

        return rootView;
    }

    private void startUploadActivity() {
        Intent intent = new Intent(getContext(), UploadActivity.class);
        startActivity(intent);
    }

    private void openLocalOrganizationActivity() {
        Intent intent = new Intent(getContext(), LocalOrganizationActivity.class);
        startActivity(intent);
    }

    private void openEducationalResourcesActivity() {
        Intent intent = new Intent(getContext(), EducationalResourcesActivity.class);
        startActivity(intent);
    }
}
