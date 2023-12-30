package com.example.safeguardapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private Button adminBtn,authBtn;
    private TextView editProfile, emergencyContactList, logoutBtn;
    private TextView profileUsername, profileRoleUser, profileEmailUser, profileContactNumUser, profileGenderUser, profileDoBUser, profileResUser;
    private ImageView uploadProfilePic;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        editProfile = view.findViewById(R.id.editProfile);
        emergencyContactList = view.findViewById(R.id.emergencyContactList);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        profileUsername = view.findViewById(R.id.profileUsername);
        profileRoleUser = view.findViewById(R.id.profileRoleUser);
        profileEmailUser = view.findViewById(R.id.profileEmailUser);
        profileContactNumUser = view.findViewById(R.id.profileContactNumUser);
        profileGenderUser = view.findViewById(R.id.profileGenderUser);
        profileDoBUser = view.findViewById(R.id.profileDoBUser);
        profileResUser = view.findViewById(R.id.profileResUser);
        uploadProfilePic = view.findViewById(R.id.uploadProfilePic);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get reference to the user data in the Firebase Realtime Database
            userRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("role").exists()) {
                            String username = dataSnapshot.child("role").getValue(String.class);
                            profileRoleUser.setText(username);
                        }

                        if (dataSnapshot.child("username").exists()) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            profileUsername.setText(username);
                        }

                        if (dataSnapshot.child("contactNum").exists()) {
                            String contactNum = dataSnapshot.child("contactNum").getValue(String.class);
                            profileContactNumUser.setText(contactNum);
                        }

                        if (dataSnapshot.child("gender").exists()) {
                            String gender = dataSnapshot.child("gender").getValue(String.class);
                            profileGenderUser.setText(gender);
                        }

                        if (dataSnapshot.child("dob").exists()) {
                            String dob = dataSnapshot.child("dob").getValue(String.class);
                            profileDoBUser.setText(dob);
                        }

                        // Load the profile image using Picasso (or any image loading library of your choice)
                        if (dataSnapshot.child("profileImageUrl").exists()) {
                            String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                            Picasso.get().load(profileImageUrl).into(uploadProfilePic);
                        }
                    }

                    // Get email and profileResUser from Firebase Authentication
                    String email = currentUser.getEmail();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String creationDate = dateFormat.format(currentUser.getMetadata().getCreationTimestamp());

                    profileEmailUser.setText(email);
                    profileResUser.setText(creationDate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        emergencyContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmergencyContactListActivity.class);
                startActivity(intent);
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
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

        /*//Just for DEMO
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
        }*/

        return view;
    }

}
