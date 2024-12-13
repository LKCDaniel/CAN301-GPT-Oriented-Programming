package com.example.xjtlumappromax.ui.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.databinding.TeacherBasicInfoBinding;

public class TeacherBasicInfoActivity extends AppCompatActivity {

    private TeacherBasicInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = TeacherBasicInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String position = intent.getStringExtra("position");
        String email = intent.getStringExtra("email");
        String photoUrl = intent.getStringExtra("photo");
        String detailsUrl = intent.getStringExtra("details");


        binding.nameText.setText(name != null ? name : "N/A");
        binding.positionText.setText(position != null ? position : "N/A");
        binding.emailText.setText(email != null ? email : "N/A");

        // Use Glide to load picture
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_photo) // default
                    .error(R.drawable.placeholder_photo) // default
                    .into(binding.teacherPhoto);
        } else {
            binding.teacherPhoto.setImageResource(R.drawable.placeholder_photo);
        }

        binding.viewMore.setOnClickListener(v -> {
            if (detailsUrl != null && !detailsUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detailsUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "No details available.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.back.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
