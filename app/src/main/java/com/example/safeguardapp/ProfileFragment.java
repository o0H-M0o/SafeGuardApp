package com.example.safeguardapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private Button adminBtn,authBtn;
    private TextView userTV, emergencyContactList, logoutBtn;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBtn = view.findViewById(R.id.logoutBtn);
        emergencyContactList = view.findViewById(R.id.emergencyContactList);

        emergencyContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmergencyContactListActivity.class);
                startActivity(intent);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //Just for DEMO
        adminBtn = view.findViewById(R.id.adminBtn);
        authBtn = view.findViewById(R.id.authBtn);

        adminBtn.setVisibility(View.GONE);
        authBtn.setVisibility(View.GONE);

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
                            case "admin":
                                adminBtn.setVisibility(View.VISIBLE);
                                authBtn.setVisibility(View.VISIBLE);
                                break;
                            case "authority":
                                authBtn.setVisibility(View.VISIBLE);
                                break;
                            case "user":
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

        //Just for DEMO
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OnlyForAuthActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
