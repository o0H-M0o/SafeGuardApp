package com.example.safeguardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateProfileActivity extends AppCompatActivity {

    private Button updateProfileBtn;
    private ImageButton cancelUpdateBtn;
    private ImageView uploadEditPic;
    private TextView TVUploadProPic;
    private EditText editProfileUsername, editContactNum, editDoB;
    private Spinner editGender;
    private ProgressBar progressBar;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        updateProfileBtn = findViewById(R.id.updateProfileBtn);
        cancelUpdateBtn = findViewById(R.id.cancelUpdateBtn);
        uploadEditPic = findViewById(R.id.uploadEditPic);
        TVUploadProPic = findViewById(R.id.TVUploadProPic);
        editProfileUsername = findViewById(R.id.editProfileUsername);
        editContactNum = findViewById(R.id.editContactNum);
        editDoB = findViewById(R.id.editDoB);
        editGender = findViewById(R.id.editGender);
        progressBar = findViewById(R.id.progressBar);

        displayExistingProfileData();

        cancelUpdateBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.genderAry,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editGender.setAdapter(adapter);

        uploadEditPic.setOnClickListener(v -> {
            // Open the image picker
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        editDoB.setOnClickListener(v -> showDatePickerDialog());

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String username, contactNum, dob, gender;
                username = editProfileUsername.getText().toString();
                contactNum = editContactNum.getText().toString();
                dob = editDoB.getText().toString();
                gender = editGender.getSelectedItem() != null ? editGender.getSelectedItem().toString() : "";

                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
                    TVUploadProPic.setTextColor(Color.RED);
                    progressBar.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                    editProfileUsername.setError("Username is required");
                    editProfileUsername.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(contactNum)) {
                    Toast.makeText(getApplicationContext(), "Please enter your contact number", Toast.LENGTH_SHORT).show();
                    editContactNum.setError("Contact number is required");
                    editContactNum.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(dob)) {
                    Toast.makeText(getApplicationContext(), "Please choose your date of birth", Toast.LENGTH_SHORT).show();
                    editDoB.setError("Date of birth is required");
                    editDoB.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (gender.equals("Gender")) {
                    Toast.makeText(getApplicationContext(), "Please choose your gender", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else{
                    updateProfile(username, contactNum, dob, gender);
                }
            }
        });
    }

    private void displayExistingProfileData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get reference to the user data in the Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("username").exists()) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            editProfileUsername.setText(username);
                        }

                        if (dataSnapshot.child("contactNum").exists()) {
                            String contactNum = dataSnapshot.child("contactNum").getValue(String.class);
                            editContactNum.setText(contactNum);
                        }

                        if (dataSnapshot.child("gender").exists()) {
                            String gender = dataSnapshot.child("gender").getValue(String.class);
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) editGender.getAdapter();
                            if (adapter != null) {
                                int position = adapter.getPosition(gender);
                                if (position >= 0) {
                                    editGender.setSelection(position);
                                }
                            }
                        }

                        if (dataSnapshot.child("dob").exists()) {
                            String dob = dataSnapshot.child("dob").getValue(String.class);
                            editDoB.setText(dob);
                        }

                        // Load the profile image using Picasso (or any image loading library of your choice)
                        if (dataSnapshot.child("profileImageUrl").exists()) {
                            String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                            Picasso.get().load(profileImageUrl).into(uploadEditPic);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    private void updateProfile(String username, String contactNum, String dob, String gender) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userId);
        userRef.child("username").setValue(username);
        userRef.child("contactNum").setValue(contactNum);
        userRef.child("dob").setValue(dob);
        userRef.child("gender").setValue(gender);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userId + ".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userRef.child("profileImageUrl").setValue(imageUrl);

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadEditPic.setImageURI(imageUri);
        }
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    month1 = month1 + 1;
                    String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1, year1);
                    editDoB.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }
}