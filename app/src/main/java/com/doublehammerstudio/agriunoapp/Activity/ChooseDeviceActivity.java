package com.doublehammerstudio.agriunoapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.doublehammerstudio.agriunoapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChooseDeviceActivity extends AppCompatActivity {
    Button device1;
    Button device2;
    Button viewer;
    Button deviceCustomIPButton;
    Button deviceRefreshButton;
    Button deviceScanFoIPButton;
    private static final String TAG = "NetScan";
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_device);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(ChooseDeviceActivity.this, "Checking For Available Devices..", Toast.LENGTH_SHORT).show();
        mDialog = new ProgressDialog(ChooseDeviceActivity.this);

        device1 = findViewById(R.id.device1Button);
        device2 = findViewById(R.id.device2Button);
        deviceCustomIPButton = findViewById(R.id.deviceCustomIPButton);
        viewer = findViewById(R.id.viewer);
        deviceRefreshButton = findViewById(R.id.deviceRefreshButton);
        deviceScanFoIPButton = findViewById(R.id.deviceScanFoIPButton);
        deviceScanFoIPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentDeviceIp = "";

            }
        });

        deviceRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mDialog.setMessage("Re-scanning the network");
                mDialog.setTitle("Scanning for available devices in the network...");
                mDialog.setCancelable(false);
                mDialog.show();

                MainActivity.currentDeviceIp = "";

                Toast.makeText(ChooseDeviceActivity.this, "Scanning for available devices",
                        Toast.LENGTH_SHORT).show();
                new CheckDeviceAvailabilityTask(device1, "192.168.254.100").execute();
                new CheckDeviceAvailabilityTask(device2, "192.168.254.101").execute();
            }
        });
        new CheckDeviceAvailabilityTask(device1, "192.168.209.224").execute();
        new CheckDeviceAvailabilityTask(device2, "192.168.254.101").execute();

        device1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentDeviceIp = "";
                new CheckDeviceTask("192.168.254.100").execute();
            }
        });

        device2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentDeviceIp = "";

                new CheckDeviceTask("192.168.254.101").execute();
            }
        });


        viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseDeviceActivity.this, MainActivity.class);
                MainActivity.currentDeviceIp = "Viewer";
                startActivity(intent);
                finish();
            }
        });
    }
    private void showCustomIPDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_edit_text, null);
        final EditText editTextIP = dialogLayout.findViewById(R.id.editTextIP);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Custom IP Address");
        builder.setView(dialogLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String input_ip = editTextIP.getText().toString();

                new CheckDeviceTask(input_ip).execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private class CheckDeviceTask extends AsyncTask<Void, Void, Boolean> {
        private String deviceIp;

        public CheckDeviceTask(String deviceIp) {
            this.deviceIp = deviceIp;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isDeviceReachable(deviceIp);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent intent = new Intent(ChooseDeviceActivity.this, MainActivity.class);
                MainActivity.currentDeviceIp = "http://" + deviceIp ;
                startActivity(intent);
                finish();
            }
        }
    }

    private class CheckDeviceAvailabilityTask extends AsyncTask<Void, Void, Boolean> {
        private Button deviceButton;
        private String deviceIp;

        public CheckDeviceAvailabilityTask(Button deviceButton, String deviceIp) {
            this.deviceButton = deviceButton;
            this.deviceIp = deviceIp;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isDeviceReachable(deviceIp);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            deviceButton.setEnabled(result);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (!result) {
                Toast.makeText(ChooseDeviceActivity.this, "Device at " + deviceIp + " is not reachable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isDeviceReachable(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + ipAddress);
            int returnVal = process.waitFor();
            return (returnVal == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

}