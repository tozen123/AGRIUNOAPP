package com.doublehammerstudio.agriunoapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.doublehammerstudio.agriunoapp.Activity.MainActivity;
import com.doublehammerstudio.agriunoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.AsyncTask;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class DataReadingFragment extends Fragment {



    private Button btnConnect;

    double calibration_value = 5.8;
    EditText nt, ph, p;

    int nitro, phosp, potas;
    ArrayList<Integer> nitrogenValues = new ArrayList<Integer>();
    ArrayList<Integer> phosphorusValues = new ArrayList<Integer>();
    ArrayList<Integer> potassiumValues = new ArrayList<Integer>();

    private Context mContext;
    private ProgressDialog mDialog;

    //rows
    TextView row1Cell141414, row1Cell46000, row1Cell00060, row1CellTotal;
    TextView row2Cell141414, row2Cell46000, row2Cell00060, row2CellTotal;
    TextView row3CellTotal141414, row3CellTotal46000, row3CellTotal0060, row3CellTotalBag;

    Button recordAgain, SaveToFarm;


    double finalAverageNitrogen;
    double finalAveragePhosphorus;
    double finalAveragePotassium;
    private ArrayList<String> listOfMunicipalityButtons = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_reading, container, false);

        nt = view.findViewById(R.id.nitrogenDataCount);
        ph = view.findViewById(R.id.phosporusDataCount);
        p = view.findViewById(R.id.potassiumDataCount);

        recordAgain = view.findViewById(R.id.btnRecordAgain);


        //Row 1
        row1Cell141414 = view.findViewById(R.id.row1Cell141414);
        row1Cell46000 = view.findViewById(R.id.row1Cell46000);
        row1Cell00060 = view.findViewById(R.id.row1Cell00060);
        row1CellTotal = view.findViewById(R.id.row1CellTotal);

        //Row 2
        row2Cell141414 = view.findViewById(R.id.row2Cell141414);
        row2Cell46000 = view.findViewById(R.id.row2Cell46000);
        row2Cell00060 = view.findViewById(R.id.row2Cell00060);
        row2CellTotal = view.findViewById(R.id.row2CellTotal);

        //Row 3
        row3CellTotal141414 = view.findViewById(R.id.row3CellTotal141414);
        row3CellTotal46000 = view.findViewById(R.id.row3CellTotal46000);
        row3CellTotal0060 = view.findViewById(R.id.row3CellTotal0060);
        row3CellTotalBag = view.findViewById(R.id.row3CellTotalBag);

        nt.setEnabled(false);
        ph.setEnabled(false);
        p.setEnabled(false);
        btnConnect = view.findViewById(R.id.btnConnect);


        listOfMunicipalityButtons.add("MERCEDES");
        listOfMunicipalityButtons.add("STA ELENA");
        listOfMunicipalityButtons.add("LABO");
        listOfMunicipalityButtons.add("SAN LORENZO");
        listOfMunicipalityButtons.add("SAN VICENTE");
        listOfMunicipalityButtons.add("VINZONS");
        listOfMunicipalityButtons.add("TALISAY");
        listOfMunicipalityButtons.add("JOSE PANGANIBAN");
        listOfMunicipalityButtons.add("CAPALONGA");
        listOfMunicipalityButtons.add("BASUD");
        listOfMunicipalityButtons.add("PARACALE");
        listOfMunicipalityButtons.add("DAET");
        SaveToFarm = view.findViewById(R.id.btnSave);

        SaveToFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nitrogenValues.isEmpty()){
                    Toast.makeText(getActivity(), "Save failed. No data is found.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(phosphorusValues.isEmpty()){
                    Toast.makeText(getActivity(), "Save failed. No data is found.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(potassiumValues.isEmpty()){
                    Toast.makeText(getActivity(), "Save failed. No data is found.", Toast.LENGTH_LONG).show();
                    return;
                }
                ChooseMunicipality();
            }
        });

        mContext = getContext();

        recordAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearData();
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "IP: " + MainActivity.currentDeviceIp, Toast.LENGTH_SHORT).show();
                Log.d("DataRead1234", "MainActivity.currentDeviceIp: "  +  MainActivity.currentDeviceIp);

                mDialog = new ProgressDialog(mContext);
                mDialog.setMessage("Communicating with the Device...");
                mDialog.setTitle("Agriuno NPK Device");
                mDialog.setCancelable(false);
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDialog.setIndeterminate(false);
                mDialog.setMax(10);
                mDialog.show();


                int count = 0;

                finalAverageNitrogen = 0.0;
                finalAveragePhosphorus = 0.0;
                finalAveragePotassium = 0.0;

                nitrogenValues.clear();
                phosphorusValues.clear();
                potassiumValues.clear();
                while (count < 10){
                    try {
                        new HttpRequestTask().execute();

                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    count++;
                }

            }
        });

        if(MainActivity.currentDeviceIp.equals("Viewer"))
        {
            recordAgain.setEnabled(false);
            SaveToFarm.setEnabled(false);
            btnConnect.setEnabled(false);
        }
        return view;
    }
    private void ClearData(){
        nt.setText("");
        ph.setText("");
        p.setText("");

        finalAverageNitrogen = 0.0;
        finalAveragePhosphorus = 0.0;
        finalAveragePotassium = 0.0;

        nitrogenValues.clear();
        phosphorusValues.clear();
        potassiumValues.clear();
        // Row 1
        row1Cell141414.setText("");
        row1Cell46000.setText("");
        row1Cell00060.setText("");
        row1CellTotal.setText("");

        // Row 2
        row2Cell141414.setText("");
        row2Cell46000.setText("");
        row2Cell00060.setText("");
        row2CellTotal.setText("");

        // Row 3
        row3CellTotal141414.setText("");
        row3CellTotal46000.setText("");
        row3CellTotal0060.setText("");
        row3CellTotalBag.setText("");

        Toast.makeText(getActivity(), "Cleared Successfully.", Toast.LENGTH_SHORT).show();
    }
    private void ChooseMunicipality(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select an Municipality");


        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);



        scrollView.addView(layout);
        builder.setView(scrollView);
        AlertDialog dialog = builder.create();

        for (String item : listOfMunicipalityButtons) {
            Button button = new Button(getContext());
            button.setText(item);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectFarmFromMunicipality(item, dialog);
                }
            });
            layout.addView(button);
        }

        dialog.show();
    }

    private void SelectFarmFromMunicipality(String municipality_name, AlertDialog dialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DashboardDataPineapple").child(municipality_name);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> farmList = new ArrayList<>();
                for (DataSnapshot farmSnapshot : snapshot.getChildren()) {
                    if (farmSnapshot.exists() && farmSnapshot.getValue() != null) {
                        String farmName = farmSnapshot.child("name").getValue(String.class);
                        String ownerName = farmSnapshot.child("ownerName").getValue(String.class);
                        String areaOfLand = farmSnapshot.child("areaOfLand").getValue(String.class);
                        farmList.add("Farm Name: " + farmName + "\nOwner: " + ownerName + "\nArea: "+ areaOfLand + " sqm");
                    }
                }

                if (farmList.isEmpty()) {
                    Toast.makeText(getContext(), "No farms available for " + municipality_name, Toast.LENGTH_SHORT).show();
                } else {
                    showFarmListDialog(municipality_name, farmList);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFarmListDialog(String municipality_name, List<String> farmList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Farms in " + municipality_name);

        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);



        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        for (String farmInfo : farmList) {
            Button button = new Button(getContext());
            button.setText(farmInfo);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] farmDetails = farmInfo.split("\n");
                    String farmName = farmDetails[0].split(": ")[1];
                    String recording = finalAverageNitrogen + ", " + finalAveragePhosphorus + ", " + finalAveragePotassium;

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("DashboardDataPineapple")
                            .child(municipality_name);

                    databaseReference.orderByChild("name").equalTo(farmName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot farmSnapshot : snapshot.getChildren()) {
                                if (farmSnapshot.child("name").getValue(String.class).equals(farmName)) {
                                    farmSnapshot.getRef().child("recording").setValue(recording);
                                    Toast.makeText(getContext(), "Recording set for " + farmName, Toast.LENGTH_SHORT).show();
                                    ClearData();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to set recording", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.dismiss();

                }
            });
            layout.addView(button);
        }

        dialog.show();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
                String esp8266Ip = String.valueOf(MainActivity.currentDeviceIp) ;


                String nitrogen = fetchValue(esp8266Ip + "/nitrogen");
                String phosphorus = fetchValue(esp8266Ip + "/phosphorus");
                String potassium = fetchValue(esp8266Ip + "/potassium");

                nitrogenValues.add(Integer.parseInt(nitrogen));
                phosphorusValues.add(Integer.parseInt(phosphorus));
                potassiumValues.add(Integer.parseInt(potassium));

                if (nitrogenValues.size() == 10) {

                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
                mDialog.setMessage("NPK READING. Iteration: " + nitrogenValues.size());
                Log.d("DataRead1234", "nitrogenValues.size(): "  +  nitrogenValues.size());

                Log.d("DataRead1234", "nitrogenValues: "  +  nitrogenValues);
                Log.d("DataRead1234", "phosphorusValues: "  +  phosphorusValues);
                Log.d("DataRead1234", "potassiumValues: "  +  potassiumValues);
                return "N: " + nitrogen + " ppm\n" +
                        "P: " + phosphorus + " ppm\n" +
                        "K: " + potassium + " ppm\n";

            } catch (Exception e) {

                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        private String fetchValue(String urlString) throws Exception {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            urlConnection.disconnect();
            return content.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            String[] values = result.split("\n");

            if (values.length == 3) {

                int sumNitrogen = 0;
                int sumPhosphorus = 0;
                int sumPotassium = 0;
                for (Integer value : nitrogenValues) {
                    sumNitrogen += value;
                }
                for (Integer value : phosphorusValues) {
                    sumPhosphorus += value;
                }
                for (Integer value : potassiumValues) {
                    sumPotassium += value;
                }

                double averageNitrogen = ((double) sumNitrogen / nitrogenValues.size()) * calibration_value;
                double averagePhosphorus = ((double) sumPhosphorus / phosphorusValues.size()) * calibration_value;
                double averagePotassium = ((double) sumPotassium / potassiumValues.size()) * calibration_value;

                finalAverageNitrogen = averageNitrogen;
                finalAveragePhosphorus = averagePhosphorus;
                finalAveragePotassium = averagePotassium;

                nt.setText(String.format("%.0f", averageNitrogen));
                ph.setText(String.format("%.0f", averagePhosphorus));
                p.setText(String.format("%.0f", averagePotassium));

                mDialog.incrementProgressBy(1);
                if (mDialog.getProgress() == mDialog.getMax()) {
                    mDialog.dismiss();
                }

                Log.d("DataRead1234", "nitrogenDataCount: "  +  nt.getText());
                Log.d("DataRead1234", "phosporusDataCount: "  +  ph.getText());
                Log.d("DataRead1234", "potassiumDataCount: "  +  p.getText());


                //14-14-14
                double[] lowest_reading = new double[3];
                lowest_reading[0] = averageNitrogen;
                lowest_reading[1] = averagePhosphorus;
                lowest_reading[2] = averagePotassium;

                double lowest_reading_value = Arrays.stream(lowest_reading).min().getAsDouble();
                double complete_value = lowest_reading_value / 0.14;
                complete_value = (complete_value / 50) / 2;

                double rounded_a_value = Double.parseDouble(String.format("%.2f", complete_value));

                row1Cell141414.setText(String.valueOf(rounded_a_value));
                row2Cell141414.setText(String.valueOf(rounded_a_value));

                //46-0-0
                double urea_value = averagePhosphorus / 0.46;
                urea_value = (urea_value / 50) / 2;

                double rounded_b_value = Double.parseDouble(String.format("%.2f", urea_value));

                row1Cell46000.setText(String.valueOf(rounded_b_value));
                row2Cell46000.setText(String.valueOf(rounded_b_value));

                //00-00-60
                double averagePotassium_subtracted = averagePotassium - lowest_reading_value;
                double potast_value = averagePotassium_subtracted / 0.60;
                potast_value = (potast_value / 50) / 2;

                double rounded_c_value = Double.parseDouble(String.format("%.2f", potast_value));

                row1Cell00060.setText(String.valueOf(rounded_c_value));
                row2Cell00060.setText(String.valueOf(rounded_c_value));

                double total141414 = rounded_a_value + rounded_a_value;
                row3CellTotal141414.setText(String.valueOf(total141414));

                double total46000 = rounded_b_value + rounded_b_value;
                row3CellTotal46000.setText(String.valueOf(total46000));

                double total00060 = rounded_c_value + rounded_c_value;
                row3CellTotal0060.setText(String.valueOf(total00060));

                double finalTotal = total141414 + total46000 + total00060;
                row3CellTotalBag.setText(String.valueOf(finalTotal));

                double total = complete_value + urea_value + potast_value;
                double rounded_total_value = Double.parseDouble(String.format("%.2f", total));
                row1CellTotal.setText(String.valueOf(rounded_total_value));
                row2CellTotal.setText(String.valueOf(rounded_total_value));

            } else {

                if (mDialog.isShowing()) {
                    Toast.makeText(getActivity(), "Error: Communication to the device have failed" +
                            ". Please Check the NPK Device. ", Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                }

                nt.setText("0");
                ph.setText("0");
                p.setText("0");
            }

        }
    }

}