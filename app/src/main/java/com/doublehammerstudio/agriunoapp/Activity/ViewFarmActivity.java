package com.doublehammerstudio.agriunoapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.doublehammerstudio.agriunoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class ViewFarmActivity extends AppCompatActivity {

    String municipality;
    String areaOfLand;
    String description;
    String name;
    String ownerName;
    String recording;
    TextView nametextView, desctextView, areaoflandtextView, ownertextView;

    TextView row1Cell141414, row1Cell46000, row1Cell00060, row1CellTotal;
    TextView row2Cell141414, row2Cell46000, row2Cell00060, row2CellTotal;
    TextView row3CellTotal141414, row3CellTotal46000, row3CellTotal0060, row3CellTotalBag;
    TextView potash, urea, complete, totalC;
    Button update, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_farm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            areaOfLand = extras.getString("farmAreaOfLand");
            description = extras.getString("farmDescription");
            name = extras.getString("farmName");
            ownerName = extras.getString("farmOwnerName");
            recording = extras.getString("farmRecording");
            municipality = extras.getString("farmMunicipality");

            Toast.makeText(ViewFarmActivity.this, "Data Loaded Successfully!", Toast.LENGTH_LONG).show();
        }

        update = findViewById(R.id.btnUpdate);
        delete = findViewById(R.id.btnDelete);

        potash = findViewById(R.id.PotashTxt);
        urea = findViewById(R.id.ureaTxt);
        complete = findViewById(R.id.CompleteTxt);
        totalC = findViewById(R.id.totalC);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(municipality.isEmpty()) {
                    Toast.makeText(ViewFarmActivity.this, "Error: Entry not found!", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewFarmActivity.this);
                builder.setTitle("Update Farm");
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_farm, null);
                builder.setView(dialogView);

                final EditText inputFarmName = dialogView.findViewById(R.id.editTextFarmName);
                final EditText inputFarmDescription = dialogView.findViewById(R.id.editTextFarmDescription);
                final EditText inputFarmAreaOfLand = dialogView.findViewById(R.id.editTextFarmAreaOfLand);
                final EditText inputFarmOwner = dialogView.findViewById(R.id.editTextFarmOwner);

                inputFarmName.setText(name);
                inputFarmDescription.setText(description);
                inputFarmAreaOfLand.setText(String.valueOf(areaOfLand));
                inputFarmOwner.setText(ownerName);

                builder.setPositiveButton("Update", (dialog, which) -> {
                    String farmName = inputFarmName.getText().toString().trim();
                    String farmDescription = inputFarmDescription.getText().toString().trim();
                    String farmAreaOfLand = inputFarmAreaOfLand.getText().toString().trim();
                    String farmOwner = inputFarmOwner.getText().toString().trim();

                    try {
                        int areaOfLand = Integer.parseInt(farmAreaOfLand);
                        updateFarmInformation(farmName, farmDescription, farmAreaOfLand, farmOwner);
                        finish();
                    } catch (NumberFormatException e) {

                        Toast.makeText(ViewFarmActivity.this, "Please put a valid integer input.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(municipality.isEmpty())
                {
                    Toast.makeText(ViewFarmActivity.this, "Error: Entry did not found!", Toast.LENGTH_LONG).show();

                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewFarmActivity.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this farm?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                .getReference("DashboardDataPineapple")
                                .child(municipality);

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String farmName = snapshot.child("name").getValue(String.class);
                                    if (farmName != null && farmName.equals(name)) {
                                        snapshot.getRef().removeValue();
                                        Toast.makeText(ViewFarmActivity.this, "Farm deleted successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        return;
                                    }
                                }
                                Toast.makeText(ViewFarmActivity.this, "Farm entry not found!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ViewFarmActivity.this, "Failed to delete farm: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //Row 1
        row1Cell141414 = findViewById(R.id.row1Cell141414);
        row1Cell46000 = findViewById(R.id.row1Cell46000);
        row1Cell00060 = findViewById(R.id.row1Cell00060);
        row1CellTotal = findViewById(R.id.row1CellTotal);

        //Row 2
        row2Cell141414 =findViewById(R.id.row2Cell141414);
        row2Cell46000 = findViewById(R.id.row2Cell46000);
        row2Cell00060 = findViewById(R.id.row2Cell00060);
        row2CellTotal = findViewById(R.id.row2CellTotal);

        //Row 3
        row3CellTotal141414 = findViewById(R.id.row3CellTotal141414);
        row3CellTotal46000 = findViewById(R.id.row3CellTotal46000);
        row3CellTotal0060 = findViewById(R.id.row3CellTotal0060);
        row3CellTotalBag = findViewById(R.id.row3CellTotalBag);


        ownertextView = findViewById(R.id.ownerName);
        nametextView = findViewById(R.id.farmName);
        desctextView = findViewById(R.id.descrip);
        areaoflandtextView = findViewById(R.id.areaofland);

        ownertextView.setText(ownerName);
        nametextView.setText(name);
        desctextView.setText(description);
        areaoflandtextView.setText(areaOfLand);

        if(!recording.isEmpty())
        {
            double averageNitrogen = Double.parseDouble(recording.split(",")[0].toString().trim());
            double averagePhosphorus = Double.parseDouble(recording.split(",")[1].toString().trim());
            double averagePotassium = Double.parseDouble(recording.split(",")[2].toString().trim());

            //first store the phosphorus in a variable

            double x_value = averagePhosphorus;
            int b_divider = 50;
            int c_divider = 2;

            double p_percentage = 0.46;
            double c_percentage = 0.60;
            double type_fertilizer_percentage = 0.14;

            //get the average readings of npk, then subtract it to the x_value variable
            double a = averageNitrogen - x_value;
            double b = averagePhosphorus;
            double c = averagePotassium - x_value;


            // for 14-14-14,
            double amt_per_ha_b =  (b / type_fertilizer_percentage) ;
            b = (amt_per_ha_b / b_divider) / c_divider;

            double rounded_b_value = Double.parseDouble(String.format("%.1f", b));

            //for 46-0-0
            double amt_per_ha_a =  (a / p_percentage) ;
            a = (amt_per_ha_a / b_divider) / c_divider;

            double rounded_a_value = Double.parseDouble(String.format("%.1f", a));

            //for 0-0-60
            double amt_per_ha_c = (c / c_percentage);
            c = Math.abs((amt_per_ha_c / b_divider) / c_divider);
            double rounded_c_value = Double.parseDouble(String.format("%.1f", c));

            double total = a + b + c;
            double rounded_total_value = Double.parseDouble(String.format("%.1f", total));
            row1Cell141414.setText(String.valueOf(rounded_b_value));
            row1Cell46000.setText(String.valueOf(rounded_a_value));
            row1Cell00060.setText(String.valueOf(rounded_c_value));
            row1CellTotal.setText(String.valueOf(rounded_total_value));

            row2Cell141414.setText(String.valueOf(rounded_b_value));
            row2Cell46000.setText(String.valueOf(rounded_a_value));
            row2Cell00060.setText(String.valueOf(rounded_c_value));
            row2CellTotal.setText(String.valueOf(rounded_total_value));

            double total141414 = rounded_b_value + rounded_b_value;
            row3CellTotal141414.setText(String.valueOf(total141414));
            double total46000 = rounded_a_value + rounded_a_value;
            row3CellTotal46000.setText(String.valueOf(total46000));
            double total00060 = rounded_c_value + rounded_c_value;
            row3CellTotal0060.setText(String.valueOf(total00060));
            double finalTotal = total141414 + total46000 + total00060;
            row3CellTotalBag.setText(String.valueOf(finalTotal));

            // Prices per kg
            double price_per_kg_46000 = 1600 / 50.0 / 1000.0;
            double price_per_kg_141414 = 1600.90 / 50.0 / 1000.0;
            double price_per_kg_0060 = 2034.23 / 50.0 / 1000.0;

            // Estimated costs
            double cost_46000 = rounded_a_value * price_per_kg_46000 * 1000;
            double cost_141414 = rounded_b_value * price_per_kg_141414 * 1000;
            double cost_0060 = rounded_c_value * price_per_kg_0060 * 1000;
            double total_cost = cost_46000 + cost_141414 + cost_0060;

            potash.setText(String.format("Muriate of Potash (0-0-60): %s per gram", String.format("%.2f PHP", cost_0060 * 2)));
            urea.setText(String.format("Urea (Granular) (46-0-0): %s per gram", String.format("%.2f PHP", cost_46000 * 2)));
            complete.setText(String.format("Complete (14-14-14): %s per gram", String.format("%.2f PHP", cost_141414 * 2)));
            totalC.setText(String.format("Total Cost: %s", String.format("%.2f PHP", total_cost * 2)));

        } else {
            Toast.makeText(ViewFarmActivity.this, "No Recordings Founded.", Toast.LENGTH_LONG).show();

        }
    }
    private void updateFarmInformation(String farmName, String farmDescription, String farmAreaOfLand, String farmOwner) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("DashboardDataPineapple")
                .child(municipality);

        Query query = databaseReference.orderByChild("name").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().child("name").setValue(farmName);
                    snapshot.getRef().child("description").setValue(farmDescription);
                    snapshot.getRef().child("areaOfLand").setValue(farmAreaOfLand);
                    snapshot.getRef().child("ownerName").setValue(farmOwner);

                    Toast.makeText(ViewFarmActivity.this, "Farm information updated successfully!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ViewFarmActivity.this, "Farm entry not found!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewFarmActivity.this, "Failed to update farm information: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}