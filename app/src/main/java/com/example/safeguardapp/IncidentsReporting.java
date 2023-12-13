package com.example.safeguardapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class IncidentsReporting extends AppCompatActivity {

    private Spinner spType;
    private EditText etDate, etTime, etLocation, etDescription;
    private ImageView ivSubProof;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidents_reporting);

        spType = findViewById(R.id.SPType);
        etDate = findViewById(R.id.ETDate);
        etTime = findViewById(R.id.ETTime);
        etLocation = findViewById(R.id.ETLocation);
        etDescription = findViewById(R.id.ETDes);
        Button btSubmit = findViewById(R.id.BTSubmit);
        ivSubProof = findViewById(R.id.IVSubProof);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Populate the spinner with choices from the string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.incident_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        // Set a click listener for the image view to pick an image from external storage
        ivSubProof.setOnClickListener(v -> {
            // Open the image picker
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Set a click listener for the date EditText to show a date picker dialog
        etDate.setOnClickListener(v -> showDatePickerDialog());

        // Set a click listener for the time EditText to show a time picker dialog
        etTime.setOnClickListener(v -> showTimePickerDialog());

        btSubmit.setOnClickListener(v -> {
            try {
                // Get user input
                String type = spType.getSelectedItem() != null ? spType.getSelectedItem().toString() : "";
                String date = etDate.getText().toString();
                String time = etTime.getText().toString();
                String location = etLocation.getText().toString();
                String description = etDescription.getText().toString();

                // Check if any of the fields are empty
                if (type.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || description.isEmpty()) {
                    showToast("Please fill in all fields.");
                    return;
                }

                // Check if an image is selected
                if (ivSubProof.getDrawable() == null) {
                    showToast("Please select an image.");
                    return;
                }

                // Convert the image to a byte array
                ivSubProof.setDrawingCacheEnabled(true);
                ivSubProof.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) ivSubProof.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStreams = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreams);
                byte[] photoData = byteArrayOutputStreams.toByteArray();

                // Upload the image to Firebase Storage
                String imageName = "incident_" + System.currentTimeMillis() + ".jpg";
                StorageReference imageRef = storageReference.child(imageName);
                UploadTask uploadTask = imageRef.putBytes(photoData);

                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return imageRef.getDownloadUrl();
                });

                // After the image is uploaded, get its download URL
                urlTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        // Create an Incident object with the image URL
                        Incident incident = new Incident(type, date, time, location, downloadUri.toString(), description);

                        // Add the incident to the database
                        String incidentId = databaseReference.child("Incidents").push().getKey();

                        assert incidentId != null;
                        databaseReference.child("Incidents").child(incidentId).setValue(incident)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        showToast("Incident submitted successfully!");
                                    } else {
                                        showToast("Failed to submit incident. Please try again.");
                                    }
                                });
                    } else {
                        showToast("Failed to upload image. Please try again.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                showToast("An unexpected error occurred.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                    etDate.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfDay) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfDay);
                    etTime.setText(formattedTime);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    // Handle the result of image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            ivSubProof.setImageURI(imageUri);
        }
    }
}
