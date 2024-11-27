package com.example.xjtlumappromax.ui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import com.example.xjtlumappromax.InteractiveImageView;
import com.example.xjtlumappromax.databinding.FragmentMapBinding;


public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private GestureDetector gestureDetector;

    // left, top, right, bottom
    private final Map<String, float[]> buildingBounds = Map.of(
            "SA", new float[]{0, 0, 0, 0},
            "SB", new float[]{0, 0, 0, 0},
            "SC", new float[]{0, 0, 0, 0},
            "SD", new float[]{0, 0, 0, 0}
    );

//    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMap;

        InteractiveImageView mapSIP = binding.mapSIP;
        mapSIP.setBuildingBounds(buildingBounds);

        mapSIP.setOnBuildingClickListener(building -> {
            Intent intent = new Intent(getContext(), SIP_BS_Activity.class);
            intent.putExtra("building", building);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}