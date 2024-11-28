package com.example.xjtlumappromax.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import com.example.xjtlumappromax.InteractiveImageView;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.databinding.FragmentMapBinding;


public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private GestureDetector gestureDetector;

    // left, top, right, bottom
    private final Map<String, float[]> buildingBounds = Map.of(
            "SA", new float[]{0, 0, 0, 0},
            "SB", new float[]{0.2f, 0.2f, 0.5f, 0.5f},
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

//        final TextView textView = binding.textMap;

        InteractiveImageView mapSIP = binding.mapSIP;
        mapSIP.setBuildingBounds(buildingBounds);
        mapSIP.setMarkerDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_location), 0.5f, 0.5f);

        mapSIP.setOnBuildingClickListener(building -> {
            Intent intent = new Intent(getContext(), BuildingActivity.class);
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