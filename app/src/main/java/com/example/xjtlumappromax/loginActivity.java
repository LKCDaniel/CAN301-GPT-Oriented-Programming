package com.example.xjtlumappromax;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView forgetTextView;
    private Button loginButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取控件
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        forgetTextView = findViewById(R.id.forget);
        loginButton = findViewById(R.id.letgo_button);

        // 初始化状态
        loginButton.setEnabled(false); // 初始时按钮禁用
        forgetTextView.setVisibility(View.GONE); // 初始时隐藏 forget

        // 添加 TextWatcher 来监听用户名和密码的输入变化
        usernameEditText.addTextChangedListener(inputWatcher);
        passwordEditText.addTextChangedListener(inputWatcher);

        Button loginButton = findViewById(R.id.letgo_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的用户名
                String username = usernameEditText.getText().toString().trim();

                // 保存 username 到 SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.apply();  // 保存数据

                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // 实时监听输入框的文本变化
    private final TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // 不需要实现
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // 获取用户名和密码的输入状态
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // 判断是否都已输入，若是，显示按钮并隐藏 forget
            if (!username.isEmpty() && !password.isEmpty()) {
                loginButton.setEnabled(true);  // 启用按钮
                forgetTextView.setVisibility(View.GONE); // 隐藏 forget 提示
            } else {
                loginButton.setEnabled(false); // 禁用按钮
                if (username.isEmpty() || password.isEmpty()) {
                    forgetTextView.setVisibility(View.VISIBLE); // 显示 forget 提示
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // 不需要实现
        }
    };

    // 校验输入是否合法
    private boolean isInputValid() {
        // 判断用户名和密码是否为空，可以在此添加更复杂的验证逻辑
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        return !username.isEmpty() && !password.isEmpty();
    }
}