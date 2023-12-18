package com.example.safeguardapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactAdapter extends ArrayAdapter<EmergencyContactModel> {

    private Context context;
    private List<EmergencyContactModel> contacts;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private List<String> contactNumbers = new ArrayList<>();

    public EmergencyContactAdapter(@NonNull Context context, List<EmergencyContactModel> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.contacts = contacts;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase
                .getReference("Registered Users")
                .child(userId)
                .child("EmergencyContacts");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactNumbers.clear(); // Clear the list before adding numbers
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EmergencyContactModel contact = snapshot.getValue(EmergencyContactModel.class);
                    if (contact != null) {
                        // Add the phone number to the list
                        contactNumbers.add(contact.getPhoneNumber());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmergencyContactModel contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        LinearLayout linearLayout = convertView.findViewById(R.id.linear);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);

        if (contact != null) {
            tvName.setText(contact.getName());
            tvPhone.setText(contact.getPhoneNumber());
        }

        linearLayout.setOnLongClickListener(view -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Remove Contact")
                    .setMessage("Are you sure want to remove this contact?")
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        removeContact(contact);
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> {
                    })
                    .show();
            return false;
        });

        return convertView;
    }

    private void removeContact(EmergencyContactModel contact) {
        String contactId = String.valueOf(contact.getId());
        databaseReference.child(contactId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    contacts.remove(contact);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Contact removed!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(context, "Failed to remove contact!", Toast.LENGTH_SHORT).show();
                });
    }

    public List<String> getContactNumbers() {
        return contactNumbers;
    }
}