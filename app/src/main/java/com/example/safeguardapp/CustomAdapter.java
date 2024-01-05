package com.example.safeguardapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

// CustomAdapter.java
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context context;
    protected List<Incidents> incidentList;  // Declare incidentList

    public CustomAdapter(Context context, List<Incidents> incidents) {
        this.context = context;
        this.incidentList = incidents;  // Initialize incidentList
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidents currentIncident = incidentList.get(position);
        holder.bind(currentIncident);
    }

    @Override
    public int getItemCount() {
        return incidentList.size();
    }

    public void setIncidentList(List<Incidents> incidents) {
        this.incidentList = incidents;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTime;
        TextView textDate;
        TextView textType;
        TextView statusTextView;
        ImageView imageIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.textTime);
            textDate = itemView.findViewById(R.id.textDate);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            textType = itemView.findViewById(R.id.textType);
            imageIcon = itemView.findViewById(R.id.imageIcon);
        }

        public void bind(Incidents incident) {
            textTime.setText("Time: " + incident.getTime());
            textDate.setText("Date: " + incident.getDate());
            statusTextView.setText("Status: " + incident.getStatus());
            textType.setText("Type: " + incident.getType()); // Update to set incident type

            // Load image using Picasso or Glide into ImageView
            Picasso.get().load(incident.getPhotoData()).into(imageIcon);
        }
    }
}
