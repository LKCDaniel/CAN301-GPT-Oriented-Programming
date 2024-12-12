package com.example.xjtlumappromax;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.xjtlumappromax.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.example.xjtlumappromax.MainActivity";
    private ActivityMainBinding binding;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);


        try {
            db = dbHelper.getDatabase();

            // 创建查询助手对象
            DatabaseQueryHelper queryHelper = new DatabaseQueryHelper(db);

            List<String> result = queryHelper.getAllDataFromTable("Profile_and_Interests", "Name");

            // 输出查询结果
            for (String data : result) {
                Log.d(TAG, "Data为: " + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

      AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_chat, R.id.navigation_friends, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);




    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();  // 关闭数据库
            Log.i("DDDB","数据库删除destroy");
        }
    }
}
