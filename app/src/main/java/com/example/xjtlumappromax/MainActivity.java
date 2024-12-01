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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "com.example.xjtlumappromax.MainActivity";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_chat, R.id.navigation_friends, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        dbHelper = new DatabaseHelper(this);

        //先复制和创建数据库
        try {
            dbHelper.createDatabase();  // 创建并复制数据库
            db = dbHelper.openDatabase();  // 打开数据库

            // 创建查询助手对象
            DatabaseQueryHelper queryHelper = new DatabaseQueryHelper(db);

            List<String> result = queryHelper.getAllDataFromTable("Profile_and_Interests", "Name");

            // 输出查询结果
            for (String data : result) {
                Log.d(TAG, "Data为: " + data);
            }

            //创建位置location和tracher名字的映射
            Map<String, String> locationTeacherMap=queryHelper.mapLocationTeacher("Teachers_Basic_Information","Location","Name");



/*
            // 输出查询结果到控制台
            for (String data : result) {
                // 使用 System.out.println() 将数据打印到控制台
                System.out.println("Data: " + data);  // 直接打印到控制台
            }
*/

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();  // 关闭数据库
        }
    }
}
