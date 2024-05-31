package com.doublehammerstudio.agriunoapp.Activity;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.doublehammerstudio.agriunoapp.Fragments.DashboardFragment;
import com.doublehammerstudio.agriunoapp.Fragments.DataReadingFragment;
import com.doublehammerstudio.agriunoapp.Adapters.MainFragmentAdapter;
import com.doublehammerstudio.agriunoapp.R;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation meowBottomNav;
    DashboardFragment dashboardFragment = new DashboardFragment();
    DataReadingFragment dataReadingFragment = new DataReadingFragment();
    List<MeowBottomNavigation.Model> bottomNavItems = new ArrayList<>();
    TextView textDeviceId;

    public static String currentDeviceIp;
    public static String currentDeviceId;
    public static String selectedPlant;
    private void fetchDeviceName() {
        new FetchDeviceNameTask().execute(currentDeviceIp);
    }
    private class FetchDeviceNameTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0] + "/deviceName");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            currentDeviceId = result.toUpperCase();
            textDeviceId.setText(currentDeviceId);
            Toast.makeText(MainActivity.this, "Device: " + currentDeviceId, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textDeviceId = findViewById(R.id.device_id);
        Toast.makeText(MainActivity.this, "currentDeviceIp: "+currentDeviceIp,
                Toast.LENGTH_LONG).show();


        if(currentDeviceIp.equals("Viewer"))
        {
            Toast.makeText(MainActivity.this, "Viewing Only",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            fetchDeviceName();
            Toast.makeText(MainActivity.this, "Connected Device at: " + currentDeviceIp,
                    Toast.LENGTH_LONG).show();
        }


        meowBottomNav = findViewById(R.id.MeowBottomNav);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        meowBottomNav.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_dashboard_24));
        meowBottomNav.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_dataset_24));


        bottomNavItems.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_dashboard_24));
        bottomNavItems.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_dataset_24));

        meowBottomNav.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, dashboardFragment).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, dataReadingFragment).commit();
                        break;


                }
                return null;
            }
        });
        MainFragmentAdapter fragmentAdapter = new MainFragmentAdapter(this);

        viewPager.setAdapter(fragmentAdapter);

        viewPager.setCurrentItem(0);

        meowBottomNav.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                viewPager.setCurrentItem(model.getId() - 1); // -1 because the fragment position starts from 0
                return null;
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                meowBottomNav.show(bottomNavItems.get(position).getId(), true);
            }
        });

        getSupportFragmentManager().beginTransaction();
    }
}