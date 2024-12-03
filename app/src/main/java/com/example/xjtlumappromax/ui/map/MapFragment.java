package com.example.xjtlumappromax.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.xjtlumappromax.InteractiveImageView;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment {

    public MapFragment instance = this;

    public MapFragment getInstance() {
        return instance;
    }

    private FragmentMapBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView gpsTextView;
    private float[] gpsPosition = {-1, -1};
    private InteractiveImageView mapView;
    private SeekBar campusBar;

    // left, top, right, bottom
    private Map<String, float[]> sipBuildings, tcBuildings;

    public MapFragment() {
        sipBuildings = new HashMap<>();
        sipBuildings.put("FB", new float[]{0.26984105f, 0.36055145f, 0.4489232f, 0.42319062f});
        sipBuildings.put("CB", new float[]{0.32945752f, 0.47716781f, 0.50128007f, 0.5732224f});
        sipBuildings.put("SA", new float[]{0.52331257f, 0.4416256f, 0.59612536f, 0.4673735f});
        sipBuildings.put("SB", new float[]{0.5341905f, 0.4771501f, 0.60621417f, 0.50060135f});
        sipBuildings.put("SC", new float[]{0.5478464f, 0.5085132f, 0.61392903f, 0.5274976f});
        sipBuildings.put("SD", new float[]{0.5571462f, 0.5342506f, 0.6214484f, 0.56072545f});
        sipBuildings.put("PB", new float[]{0.6273831f, 0.43615785f, 0.69762635f, 0.46320158f});
        sipBuildings.put("MA", new float[]{0.7187953f, 0.42824593f, 0.7941841f, 0.45585856f});
        sipBuildings.put("MB", new float[]{0.74094063f, 0.46635115f, 0.83353966f, 0.48950738f});
        sipBuildings.put("EB", new float[]{0.76012707f, 0.51266366f, 0.8566846f, 0.55682695f});
        sipBuildings.put("EE", new float[]{0.65704125f, 0.52834f, 0.7361864f, 0.5591237f});
        sipBuildings.put("IR", new float[]{0.4744663f, 0.60476154f, 0.5318425f, 0.6702059f});
        sipBuildings.put("BS", new float[]{0.43686968f, 0.7009896f, 0.5223472f, 0.8071418f});
        sipBuildings.put("HS", new float[]{0.6028813f, 0.74457353f, 0.6725246f, 0.7967858f});
        sipBuildings.put("IA", new float[]{0.59259045f, 0.6604293f, 0.67332035f, 0.72070086f});
        sipBuildings.put("ES", new float[]{0.47127774f, 0.841381f, 0.57911766f, 0.89703816f});
        sipBuildings.put("DB", new float[]{0.61325216f, 0.81463224f, 0.71792275f, 0.8674134f});
        sipBuildings.put("GM", new float[]{0.8144167f, 0.67424095f, 0.86941934f, 0.7798136f});

        tcBuildings = new HashMap<>();
        tcBuildings.put("A", new float[]{0.6021904f, 0.6692679f, 0.7927268f, 0.83774036f});
        tcBuildings.put("B", new float[]{0.7063885f, 0.52610606f, 0.90686935f, 0.6691743f});
        tcBuildings.put("C", new float[]{0.6359312f, 0.31670865f, 0.7664283f, 0.45260242f});
        tcBuildings.put("D", new float[]{0.44388962f, 0.28983936f, 0.57984954f, 0.43484196f});
        tcBuildings.put("E", new float[]{0.28956306f, 0.34983805f, 0.43991548f, 0.5029999f});
        tcBuildings.put("F", new float[]{0.23547691f, 0.6148731f, 0.43000403f, 0.72340524f});
        tcBuildings.put("G", new float[]{0.37987605f, 0.7253044f, 0.52229613f, 0.87656707f});
        tcBuildings.put("GM", new float[]{0.061872672f, 0.2159123f, 0.39036414f, 0.30524206f});
    }

    public static double dmsToDecimal(int degrees, int minutes, int seconds) {
        return degrees + (minutes / 60.0) + (seconds / 3600.0);
    }

    // Method to calculate transformation parameters and return transformed coordinates
    public static float[] transformGpsToImageCoord(double lat, double lon) {
        // Known points (GPS to Image mapping)
        // Point 1: GPS (31°16'17"N, 120°44'06"E) -> Image coordinates (x: 0.46166694, y: 0.75392914)
        // Point 2: GPS (31°16'31"N, 120°44'08"E) -> Image coordinates (x: 0.56584126, y: 0.45308766)

        // Convert known GPS points to decimal
        double lat1 = dmsToDecimal(31, 16, 17);
        double lon1 = dmsToDecimal(120, 44, 6);
        double lat2 = dmsToDecimal(31, 16, 31);
        double lon2 = dmsToDecimal(120, 44, 8);

        // Known image coordinates
        float x1 = 0.46166694f, y1 = 0.75392914f;
        float x2 = 0.56584126f, y2 = 0.45308766f;

        // Calculate the slopes (m_latitude, m_longitude)
        float m_latitude = (float) ((y2 - y1) / (lat2 - lat1));
        float m_longitude = (float) ((x2 - x1) / (lon2 - lon1));

        // Calculate the intercepts (b_latitude, b_longitude)
        float b_latitude = y1 - m_latitude * (float) lat1;
        float b_longitude = x1 - m_longitude * (float) lon1;

        // Transform the input GPS coordinates (lat, lon) to image coordinates (x, y)
        float transformedY = m_latitude * (float) lat + b_latitude;
        float transformedX = m_longitude * (float) lon + b_longitude;
        System.out.println(transformedX);
        System.out.println(transformedY);
        return new float[]{transformedX, transformedY};
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.map;
        campusBar = binding.campusBar;
        mapView.setImageResource(R.drawable.map_sip);
        mapView.setBounds(sipBuildings);

        campusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (seekBar.getProgress()) {
                    case 0:
                        mapView.setImageResource(R.drawable.map_sip);
                        mapView.setBounds(sipBuildings);
                        break;
                    case 1:
                        mapView.setImageResource(R.drawable.map_tc);
                        mapView.setBounds(tcBuildings);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        mapView.setMarkerDraw(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_location));


        mapView.setOnBoundClickListener(building -> {
            Intent intent = new Intent(getContext(), BuildingActivity.class);
            intent.putExtra("building", building);
            startActivity(intent);
        });

        gpsTextView = binding.gpsTextView;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);  // 每秒更新一次位置
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    gpsTextView.setText("Current GPS: " + latitude + ", " + longitude);
                    gpsPosition = transformGpsToImageCoord(longitude, latitude);
                    if (gpsPosition[0] >= 0 && gpsPosition[0] <= 1 && gpsPosition[0] >= 0 && gpsPosition[0] <= 1) {
                        if (!mapView.isGpsDrawn()) {
                            mapView.setGpsDraw(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gps));
                        }
                    } else {
                        mapView.cancelGpsDraw();
                    }
                    mapView.setGpsPosition(gpsPosition[0], gpsPosition[1]);
                }
            }
        };
        startLocationUpdates();
        return root;
    }

    public String getLocationName() {
        String s = mapView.getBound(gpsPosition[0], gpsPosition[1]);
        if (gpsPosition[0] >= 0 && gpsPosition[0] <= 1 && gpsPosition[0] >= 0 && gpsPosition[0] <= 1) {
            if (s.isEmpty()) {
                return "SIP campus";
            } else {
                return "SIP campus, " + s + " building";
            }
        }
        return "Not in school.";
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //获取位置权限
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
