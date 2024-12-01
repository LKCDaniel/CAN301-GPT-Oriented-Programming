package com.example.xjtlumappromax.ui.map;

import android.Manifest;
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

import java.util.Map;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView gpsTextView;

    // left, top, right, bottom
    private final Map<String, float[]> sipBuildings = Map.of(
            "SA", new float[]{0, 0, 0, 0},
            "SB", new float[]{0.2f, 0.2f, 0.5f, 0.5f},
            "SC", new float[]{0, 0, 0, 0},
            "SD", new float[]{0.6f, 0.6f, 1, 1}
    ), tcBuildings = Map.of(
            "A", new float[]{0, 0, 0, 0},
            "Big", new float[]{0.2f, 0.2f, 0.5f, 0.5f}
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        InteractiveImageView map = binding.map;
        SeekBar campusBar = binding.campusBar;
        map.setImageResource(R.drawable.map_sip);
        map.setBounds(sipBuildings);

        campusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch(seekBar.getProgress()) {
                    case 0:
                        map.setImageResource(R.drawable.map_sip);
                        map.setBounds(sipBuildings);
                        break;
                    case 1:
                        map.setImageResource(R.drawable.image_test);
                        map.setBounds(tcBuildings);
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

        map.setMarkerDraw(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_location));


        map.setOnBoundClickListener(building -> {
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
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    gpsTextView.setText("Current GPS: " + latitude + ", " + longitude);
//                    if (!map.isGpsDrawn()) {
//                        map.setGpsDraw(getResources().getDrawable(R.drawable.ic_location));
//                    }
//                    map.setGpsPosition((float) longitude, (float) latitude);
                }
            }
        };
        startLocationUpdates();
        return root;
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
