package com.example.xjtlumappromax.ui.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

        // 初始化 ViewBinding
        binding = TeacherBasicInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取 Intent 中传递的数据
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String position = intent.getStringExtra("position");
        String email = intent.getStringExtra("email");
        String photoUrl = intent.getStringExtra("photo"); // 例如 "http://example.com/jianjun_chen.jpg"
        String detailsUrl = intent.getStringExtra("details");

        Log.i("啊啊啊",position);
        // 设置数据到 UI 组件
        binding.nameText.setText(name != null ? name : "N/A");
        binding.positionText.setText(position != null ? position : "N/A");
        binding.emailText.setText(email != null ? email : "N/A");

        // 加载教师照片使用 Glide
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_photo) // 占位图
                    .error(R.drawable.placeholder_photo) // 错误图
                    .into(binding.teacherPhoto);
        } else {
            // 如果 photoUrl 为空，使用占位图
            binding.teacherPhoto.setImageResource(R.drawable.placeholder_photo);
        }

        // 设置 "View More Details" 按钮点击事件
        binding.viewMore.setOnClickListener(v -> {
            if (detailsUrl != null && !detailsUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detailsUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "No details available.", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置返回按钮点击事件
        binding.back.setOnClickListener(v -> finish());
    }
}
