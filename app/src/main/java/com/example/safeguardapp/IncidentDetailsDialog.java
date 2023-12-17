package com.example.safeguardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class IncidentDetailsDialog {

    public static void show(Context context, Incident incident, String markerLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_incident_details_dialog, null);

        setupIncidentDetails(view, incident, context);

        builder.setView(view);
        builder.setTitle("Incident Details");

        // Set the "Close" button
        builder.setNegativeButton("Close", null);

        // Set the "View More" button
        builder.setPositiveButton("View More", (dialog, which) -> {
            // Navigate to IncidentDetail activity
            navigateToIncidentDetail(context, markerLocation);
        });

        builder.show();
    }

    private static void navigateToIncidentDetail(Context context, String location) {
        Intent intent = new Intent(context, IncidentDetail.class);
        intent.putExtra("location", location);
        context.startActivity(intent);
    }

    private static void setupIncidentDetails(View view, Incident incident, Context context) {
        ImageView ivPhoto = view.findViewById(R.id.IVPhoto);
        TextView tvType = view.findViewById(R.id.TVType);
        TextView tvLocation = view.findViewById(R.id.TVLocation);
        TextView tvDate = view.findViewById(R.id.TVDate);
        TextView tvTime = view.findViewById(R.id.TVTime);

        // Set incident details
        Picasso.get().load(incident.getPhotoData()).into(ivPhoto); // Assuming incident.getPhotoData() contains the image URL
        tvType.setText("Type: " + incident.getType());
        tvLocation.setText("Location: " + incident.getLocation());
        tvDate.setText("Date: " + incident.getDate());
        tvTime.setText("Time: " + incident.getTime());
    }
}