package com.doublehammerstudio.agriunoapp.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doublehammerstudio.agriunoapp.Adapters.ButtonAdapter;
import com.doublehammerstudio.agriunoapp.Activity.MainActivity;
import com.doublehammerstudio.agriunoapp.Objects.PieChartEntry;
import com.doublehammerstudio.agriunoapp.R;
import com.doublehammerstudio.agriunoapp.Activity.ViewMunicipalityFarms;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> buttonList;
    private PieChart pieChart;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Set number of columns here
        buttonList = new ArrayList<>();

        buttonList.add("MERCEDES");
        buttonList.add("STA ELENA");
        buttonList.add("LABO");
        buttonList.add("SAN LORENZO");
        buttonList.add("SAN VICENTE");
        buttonList.add("VINZONS");
        buttonList.add("TALISAY");
        buttonList.add("JOSE PANGANIBAN");
        buttonList.add("CAPALONGA");
        buttonList.add("BASUD");
        buttonList.add("PARACALE");
        buttonList.add("DAET");

        buttonAdapter = new ButtonAdapter(buttonList, item -> {
            Toast.makeText(getContext(), item, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), ViewMunicipalityFarms.class);
            intent.putExtra("municipality_name", item);
            startActivity(intent);

        });
        recyclerView.setAdapter(buttonAdapter);


        pieChart = view.findViewById(R.id.pieChart);

        databaseReference =
                FirebaseDatabase.getInstance().getReference().child("DashboardData"+ MainActivity.selectedPlant);

        fetchDataFromFirebase();
//
//        ArrayList<PieChartEntry> entries = new ArrayList<>();
//
//        /*entries.add(new PieChartEntry(8.3f, "MERCEDES", Color.parseColor("#73E333")));
//        entries.add(new PieChartEntry(8.3f, "STA. ELENA", Color.parseColor("#E6E879")));
//        entries.add(new PieChartEntry(8.3f, "LABO", Color.parseColor("#FFCB76")));
//        entries.add(new PieChartEntry(8.3f, "SAN LORENZO", Color.parseColor("#F79150")));
//        entries.add(new PieChartEntry(8.3f, "SAN VICENTE", Color.parseColor("#DE4F45")));
//        entries.add(new PieChartEntry(8.3f, "VINZONS", Color.parseColor("#D0005F")));
//        entries.add(new PieChartEntry(8.3f, "TALISAY", Color.parseColor("#D0005F")));
//        entries.add(new PieChartEntry(8.3f, "JOSE PANGANIBAN", Color.parseColor("#9B57CC")));
//        entries.add(new PieChartEntry(8.3f, "CAPALONGA", Color.parseColor("#7E80E7")));
//        entries.add(new PieChartEntry(8.3f, "PARACALE", Color.parseColor("#65A6FA")));
//        entries.add(new PieChartEntry(8.3f, "BASUD", Color.parseColor("#49C3FB")));
//        entries.add(new PieChartEntry(8.3f, "DAET", Color.parseColor("#00CADC")));*/
//
//        List<PieEntry> pieEntries = new ArrayList<>();
//        for (PieChartEntry entry : entries) {
//            pieEntries.add(new PieEntry(entry.getNumber(), entry.getName()));
//        }
//        List<Integer> colors = new ArrayList<>();
//        for (PieChartEntry entry : entries) {
//            colors.add(entry.getColor());
//        }
//
//
//        PieDataSet dataSet = new PieDataSet(pieEntries, "");
//        dataSet.setColors(colors);
//
//        pieChart.setExtraOffsets(5, 10, 5, 5); // Add extra padding around the chart
//        pieChart.setEntryLabelTextSize(12f); // Set text size for labels
//        pieChart.setDrawEntryLabels(true); // Enable drawing labels for chart entries
//        pieChart.setEntryLabelColor(Color.BLACK); // Set label color
//        pieChart.setCenterTextSize(12f);
//
//        PieData data = new PieData(dataSet);
//        pieChart.setData(data);
//
//        pieChart.getDescription().setEnabled(false);
//        pieChart.invalidate();

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, List<String>> dataMap = (Map<String, List<String>>) dataSnapshot.getValue();
                    processDashboardData(dataMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "Error fetching data", databaseError.toException());
            }
        });
    }

    private void processDashboardData(Map<String, List<String>> dataMap) {
        if (dataMap == null) return;

        List<PieChartEntry> entries = new ArrayList<>();

        String[] colorsArray = {"#73E333", "#E6E879", "#FFCB76", "#F79150", "#DE4F45", "#D0005F", "#9B57CC", "#7E80E7", "#65A6FA", "#49C3FB", "#00CADC"};
        int colorIndex = 0;

        int totalFarms = 0;
        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            totalFarms += entry.getValue() == null ? 0 : entry.getValue().size() - 1; // Subtract 1 for the null entry
        }

        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            String location = entry.getKey();
            int count = entry.getValue() == null ? 0 : entry.getValue().size() - 1; // Subtract 1 for the null entry

            if (count > 0) {
                float percentage = ((float) count / totalFarms) * 100;
                entries.add(new PieChartEntry(percentage, location, Color.parseColor(colorsArray[colorIndex % colorsArray.length])));
                colorIndex++;
            }
        }

        updatePieChart(entries);
    }

    private void updatePieChart(List<PieChartEntry> entries) {
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (PieChartEntry entry : entries) {
            pieEntries.add(new PieEntry(entry.getNumber(), entry.getName()));
            colors.add(entry.getColor());
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors);

        pieChart.setExtraOffsets(5, 10, 5, 5); // Add extra padding around the chart
        pieChart.setEntryLabelTextSize(12f); // Set text size for labels
        pieChart.setDrawEntryLabels(true); // Enable drawing labels for chart entries
        pieChart.setEntryLabelColor(Color.BLACK); // Set label color
        pieChart.setCenterTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}

