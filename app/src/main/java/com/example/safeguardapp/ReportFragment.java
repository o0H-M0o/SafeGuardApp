package com.example.safeguardapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class ReportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        ImageView ivMakeReport = view.findViewById(R.id.IVMReport);
        ivMakeReport.setOnClickListener(v -> openIncidentReporting());

        ImageView ivTrackingReport = view.findViewById(R.id.IVTReport);
        ivTrackingReport.setOnClickListener(v -> openReportList());

        return view;
    }

    private void openIncidentReporting() {
        Intent intent = new Intent(getActivity(), IncidentsReporting.class);
        startActivity(intent);
    }

    private void openReportList() {
        Intent intent = new Intent(getActivity(), MainActivityForTracking.class);
        startActivity(intent);
    }
}
