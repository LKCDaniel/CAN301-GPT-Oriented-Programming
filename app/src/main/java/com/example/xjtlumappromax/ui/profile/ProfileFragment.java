package com.example.xjtlumappromax.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.xjtlumappromax.MainActivity;
import com.example.xjtlumappromax.databinding.FragmentProfileBinding;
import com.example.xjtlumappromax.ui.map.MapFragment;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private EditText nameEditText, genderEditText, majorEditText, hobbyEditText, locationEditText;
    private Button saveButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Use ViewBinding to access the views
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the EditText views using binding
        nameEditText = binding.nameEdit;
        genderEditText = binding.genderEdit;
        majorEditText = binding.majorEdit;
        hobbyEditText = binding.hobbyEdit;
        locationEditText = binding.locationEdit;
        saveButton = binding.saveButton;

        // Disable editing for the location EditText
        locationEditText.setFocusable(false);
        locationEditText.setClickable(false);

        // Get Location from MapFragment using Singleton
        MapFragment mapFragment = MapFragment.getInstance();
        String location = mapFragment.getLocationName();
        locationEditText.setText("Location: " + location);  // Update location field (non-editable)

        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input from EditTexts (without prefixes)
                String name = nameEditText.getText().toString().trim().replace("Name: ", ""); // Remove the "Name: " prefix
                String gender = genderEditText.getText().toString().trim().replace("Gender: ", ""); // Remove the "Gender: " prefix
                String major = majorEditText.getText().toString().trim().replace("Major: ", ""); // Remove the "Major: " prefix
                String hobby = hobbyEditText.getText().toString().trim().replace("Hobby: ", ""); // Remove the "Hobby: " prefix

                // Save data (without location, since it's not editable)
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Userprofile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name", name);
                editor.putString("gender", gender);
                editor.putString("major", major);
                editor.putString("hobby", hobby);
                editor.apply();  // Asynchronously save data

                // Optional: Show a Toast to confirm saving
                Toast.makeText(getActivity(), "Information saved!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get location from MapFragment (reload it every time)
        MapFragment mapFragment = MapFragment.getInstance();
        String location = mapFragment.getLocationName();  // Get location

        // Load saved data for other fields (except location)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Userprofile", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString("name", MainActivity.userName);
        String gender = sharedPreferences.getString("gender", "Male");
        String major = sharedPreferences.getString("major", "Computer Science");
        String hobby = sharedPreferences.getString("hobby", "Reading");

        // Update EditText fields with saved data (add prefix)
        nameEditText.setText("Name: " + name);
        genderEditText.setText("Gender: " + gender);
        majorEditText.setText("Major: " + major);
        hobbyEditText.setText("Hobby: " + hobby);

        // Update location field (non-editable)
        locationEditText.setText("Location: " + location); // This will update the location each time you load the fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Cleanup binding when the view is destroyed
    }
}

