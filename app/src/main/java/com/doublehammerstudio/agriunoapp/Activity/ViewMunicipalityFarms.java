package com.doublehammerstudio.agriunoapp.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doublehammerstudio.agriunoapp.Adapters.FarmAdapter;
import com.doublehammerstudio.agriunoapp.Objects.Farm;
import com.doublehammerstudio.agriunoapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMunicipalityFarms extends AppCompatActivity {

    private FarmAdapter farmAdapter;
    private List<Farm> farmList;
    private DatabaseReference databaseReference;
    String municipalityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_municipality_farms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         municipalityName = getIntent().getStringExtra("municipality_name");

        TextView municipalityNameTextView = findViewById(R.id.municipalityNameTextView);
        municipalityNameTextView.setText(municipalityName);

        databaseReference = FirebaseDatabase.getInstance().getReference(
                "DashboardData" + MainActivity.selectedPlant).child(municipalityName);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        farmList = new ArrayList<>();
        farmAdapter = new FarmAdapter(farmList, this);
        recyclerView.setAdapter(farmAdapter);

        fetchFarmsFromFirebase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddFarmDialog());
    }

    private void fetchFarmsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                farmList.clear();
                int totalFarmCount = 0;
                for (DataSnapshot farmSnapshot : dataSnapshot.getChildren()) {
                    if (farmSnapshot.getValue() != null) {
                        String farmName = farmSnapshot.child("name").getValue(String.class);
                        String farmDescription = farmSnapshot.child("description").getValue(String.class);
                        String farmAreaOfLand = farmSnapshot.child("areaOfLand").getValue(String.class);
                        String farmOwner = farmSnapshot.child("ownerName").getValue(String.class);
                        String farmRecording = farmSnapshot.child("recording").getValue(String.class);
                        String farmMunicipality = farmSnapshot.child("municipality").getValue(String.class);
                        if (farmName != null) {
                            farmList.add(new Farm(farmName, farmDescription, farmAreaOfLand, farmOwner, farmRecording, farmMunicipality));
                            totalFarmCount++;
                        }
                    }
                }

                TextView totalFarmCountTextView = findViewById(R.id.totalFarmCountTextView);

                if(totalFarmCount == 0){
                    totalFarmCountTextView.setText("EMPTY: THIS MUNICIPALITY HAS NO RECORDS IN " +
                            "THE " +
                            "DATABASE.");
                } else {
                    totalFarmCountTextView.setText(String.valueOf(totalFarmCount));
                }


                farmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    private void showAddFarmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Farm");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_farm, null);
        builder.setView(dialogView);

        final EditText inputFarmName = dialogView.findViewById(R.id.editTextFarmName);
        final EditText inputFarmDescription = dialogView.findViewById(R.id.editTextFarmDescription);
        final EditText inputFarmAreaOfLand = dialogView.findViewById(R.id.editTextFarmAreaOfLand);
        final EditText inputFarmOwner = dialogView.findViewById(R.id.editTextFarmOwner);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String farmName = inputFarmName.getText().toString().trim();
            String farmDescription = inputFarmDescription.getText().toString().trim();
            String farmAreaOfLand = inputFarmAreaOfLand.getText().toString().trim();
            String farmOwner = inputFarmOwner.getText().toString().trim();
            try {
                int areaOfLand = Integer.valueOf(farmAreaOfLand);
            } catch (NumberFormatException e) {
                Toast.makeText(ViewMunicipalityFarms.this, "Please put a valid integer input.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!farmName.isEmpty()) {
                addFarmToFirebase(farmName, farmDescription, farmAreaOfLand, farmOwner);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addFarmToFirebase(String farmName, String farmDescription, String areaOfLand, String ownerName) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long farmCount = dataSnapshot.getChildrenCount();
                String newKey = String.valueOf(farmCount + 1);
                DatabaseReference newFarmRef = databaseReference.child(newKey);
                newFarmRef.child("name").setValue(farmName);
                newFarmRef.child("description").setValue(farmDescription);
                newFarmRef.child("areaOfLand").setValue(areaOfLand);
                newFarmRef.child("ownerName").setValue(ownerName);
                newFarmRef.child("municipality").setValue(municipalityName);
                newFarmRef.child("recording").setValue("")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                farmList.add(new Farm(farmName.toUpperCase(), farmDescription, areaOfLand, ownerName, "", municipalityName));
                                Toast.makeText(ViewMunicipalityFarms.this, "Farm successfully added.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
}
