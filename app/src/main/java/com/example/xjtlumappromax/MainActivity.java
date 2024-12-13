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


    public static String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

        userName = getIntent().getExtras().getString("username", "User");
        Log.i(TAG, "Username: " + userName);

        try {
            db = dbHelper.getDatabase();

            DatabaseQueryHelper queryHelper = new DatabaseQueryHelper(db);

            List<String> result = queryHelper.getAllDataFromTable("Profile_and_Interests", "Name");
            for (String data : result) {
                Log.d(TAG, "Data is: " + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userName = getIntent().getExtras().getString("username", "User");
        Log.i(TAG, "Username: " + userName);

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
            db.close();
        }
    }
}
