package com.example.safeguardapp;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class EmergencyContactListActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 1;
    private static final int READ_CONTACTS_PERMISSION_CODE = 101;

    private EmergencyContactAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private List<EmergencyContactModel> contactList;
    private ListView contactListView;
    private FloatingActionButton addContactButton;
    private ImageView backProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_list);

        contactListView = findViewById(R.id.contactListView);
        addContactButton = findViewById(R.id.addContactButton);
        backProfile = findViewById(R.id.backProfile);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase
                .getReference("Registered Users")
                .child(userId)
                .child("EmergencyContacts");

        contactList = new ArrayList<>();
        adapter = new EmergencyContactAdapter(this, contactList);
        contactListView.setAdapter(adapter);

        retrieveContactsFromFirebase();

        addContactButton.setOnClickListener(view -> checkContactPermission());

        backProfile.setOnClickListener(v -> onBackPressed());
    }

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSION_CODE);
        } else {
            // Permission is already granted, proceed with contact selection
            openContactPicker();
        }
    }

    private void retrieveContactsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);

                    if (name != null && phoneNumber != null) {
                        EmergencyContactModel contact = new EmergencyContactModel(0, name, phoneNumber);
                        contactList.add(contact);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, perform contact selection logic
                openContactPicker();
            } else {
                // Permission denied, inform the user or handle it gracefully
                Toast.makeText(this, "Permission Denied: Can't access contacts", Toast.LENGTH_SHORT).show();
            }
        }
        // Handle other permission request results if needed
    }

    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = getContentResolver().query(contactData, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String phoneNumber = null;
                @SuppressLint("Range") String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone != null && hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null
                    );

                    if (phones != null && phones.moveToFirst()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phones.close();
                    }
                }
                cursor.close();

                EmergencyContactModel newContact = new EmergencyContactModel(0, contactName, phoneNumber);
                databaseReference.push().setValue(newContact);
            }
        }
    }
}