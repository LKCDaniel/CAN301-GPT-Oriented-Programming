package com.example.xjtlumappromax.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.xjtlumappromax.DatabaseHelper;
import com.example.xjtlumappromax.InteractiveImageView;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    //按钮
    Spinner teacherSpinner; // 声明为全局变量
    SearchView searchView;
    Button searchButton;


    public static MapFragment instance;

    public static MapFragment getInstance() {
        return instance;
    }


    //原有的数据库
    private SQLiteDatabase database;


    private FragmentMapBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView gpsTextView;
    private float[] gpsPosition = {-1, -1};
    private InteractiveImageView mapView;
    private SeekBar campusBar;

    // left, top, right, bottom
    private Map<String, float[]> sipBuildings, tcBuildings;

    public MapFragment() {
        instance = this;

        sipBuildings = new HashMap<>();
        sipBuildings.put("FB", new float[]{0.26984105f, 0.36055145f, 0.4489232f, 0.42319062f});
        sipBuildings.put("CB", new float[]{0.32945752f, 0.47716781f, 0.50128007f, 0.5732224f});
        sipBuildings.put("SA", new float[]{0.52331257f, 0.4416256f, 0.59612536f, 0.4673735f});
        sipBuildings.put("SB", new float[]{0.5341905f, 0.4771501f, 0.60621417f, 0.50060135f});
        sipBuildings.put("SC", new float[]{0.5478464f, 0.5085132f, 0.61392903f, 0.5274976f});
        sipBuildings.put("SD", new float[]{0.5571462f, 0.5342506f, 0.6214484f, 0.56072545f});
        sipBuildings.put("PB", new float[]{0.6273831f, 0.43615785f, 0.69762635f, 0.46320158f});
        sipBuildings.put("MA", new float[]{0.7187953f, 0.42824593f, 0.7941841f, 0.45585856f});
        sipBuildings.put("MB", new float[]{0.74094063f, 0.46635115f, 0.83353966f, 0.48950738f});
        sipBuildings.put("EB", new float[]{0.76012707f, 0.51266366f, 0.8566846f, 0.55682695f});
        sipBuildings.put("EE", new float[]{0.65704125f, 0.52834f, 0.7361864f, 0.5591237f});
        sipBuildings.put("IR", new float[]{0.4744663f, 0.60476154f, 0.5318425f, 0.6702059f});
        sipBuildings.put("BS", new float[]{0.43686968f, 0.7009896f, 0.5223472f, 0.8071418f});
        sipBuildings.put("HS", new float[]{0.6028813f, 0.74457353f, 0.6725246f, 0.7967858f});
        sipBuildings.put("IA", new float[]{0.59259045f, 0.6604293f, 0.67332035f, 0.72070086f});
        sipBuildings.put("ES", new float[]{0.47127774f, 0.841381f, 0.57911766f, 0.89703816f});
        sipBuildings.put("DB", new float[]{0.61325216f, 0.81463224f, 0.71792275f, 0.8674134f});
        sipBuildings.put("GM", new float[]{0.8144167f, 0.67424095f, 0.86941934f, 0.7798136f});

        tcBuildings = new HashMap<>();
        tcBuildings.put("A", new float[]{0.6021904f, 0.6692679f, 0.7927268f, 0.83774036f});
        tcBuildings.put("B", new float[]{0.7063885f, 0.52610606f, 0.90686935f, 0.6691743f});
        tcBuildings.put("C", new float[]{0.6359312f, 0.31670865f, 0.7664283f, 0.45260242f});
        tcBuildings.put("D", new float[]{0.44388962f, 0.28983936f, 0.57984954f, 0.43484196f});
        tcBuildings.put("E", new float[]{0.28956306f, 0.34983805f, 0.43991548f, 0.5029999f});
        tcBuildings.put("F", new float[]{0.23547691f, 0.6148731f, 0.43000403f, 0.72340524f});
        tcBuildings.put("G", new float[]{0.37987605f, 0.7253044f, 0.52229613f, 0.87656707f});
        tcBuildings.put("GM", new float[]{0.061872672f, 0.2159123f, 0.39036414f, 0.30524206f});
    }

    public static double dmsToDecimal(int degrees, int minutes, int seconds) {
        return degrees + (minutes / 60.0) + (seconds / 3600.0);
    }

    // Method to calculate transformation parameters and return transformed coordinates
    public static float[] transformGpsToImageCoord(double lat, double lon) {
        // Known points (GPS to Image mapping)
        // Point 1: GPS (31°16'17"N, 120°44'06"E) -> Image coordinates (x: 0.46166694, y: 0.75392914)
        // Point 2: GPS (31°16'31"N, 120°44'08"E) -> Image coordinates (x: 0.56584126, y: 0.45308766)

        // Convert known GPS points to decimal
        double lat1 = dmsToDecimal(31, 16, 17);
        double lon1 = dmsToDecimal(120, 44, 6);
        double lat2 = dmsToDecimal(31, 16, 31);
        double lon2 = dmsToDecimal(120, 44, 8);

        // Known image coordinates
        float x1 = 0.46166694f, y1 = 0.75392914f;
        float x2 = 0.56584126f, y2 = 0.45308766f;

        // Calculate the slopes (m_latitude, m_longitude)
        float m_latitude = (float) ((y2 - y1) / (lat2 - lat1));
        float m_longitude = (float) ((x2 - x1) / (lon2 - lon1));

        // Calculate the intercepts (b_latitude, b_longitude)
        float b_latitude = y1 - m_latitude * (float) lat1;
        float b_longitude = x1 - m_longitude * (float) lon1;

        // Transform the input GPS coordinates (lat, lon) to image coordinates (x, y)
        float transformedY = m_latitude * (float) lat + b_latitude;
        float transformedX = m_longitude * (float) lon + b_longitude;
        System.out.println(transformedX);
        System.out.println(transformedY);
        return new float[]{transformedX, transformedY};
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 在 onCreate 中获取 Context 并初始化数据库
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Context is null, cannot initialize DatabaseHelper.");
        }

        if (context != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            database = dbHelper.getReadableDatabase(); // 获取数据库
            Log.i("database数据库","数据库成功初始化");
        }


        List<String> testname=fuzzySearchName("Li");
        Log.i("数据库测试结果", testname.toString());
        //如果logcat中出现了[Dawei Liu, Lijie Yao, Lingyun Yu, Nanlin Jin,这样就成功了
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        // 搜索按钮
        searchButton = rootView.findViewById(R.id.searchButton);
        searchView = rootView.findViewById(R.id.searchView);
        teacherSpinner = rootView.findViewById(R.id.teacherSpinner);
        Log.i("Button状态", searchButton == null ? "searchButton 未找到" : "searchButton 已初始化");

// 设置搜索按钮点击事件
// 设置搜索按钮点击事件 (使用 Lambda 表达式)
        searchButton.setOnClickListener(v -> {
            String query = searchView.getQuery().toString().trim(); // 获取并去除首尾空格
            Log.i("searchButton语句为", query.toString());

            if (!query.isEmpty()) {
                List<String> results = fuzzySearchName(query); // 调用模糊查询方法
                Log.i("searchButton结果", results.toString());
                updateTeacherSpinner(results); // 更新 Spinner 的数据
            } else {
                Toast.makeText(getContext(), "请输入搜索内容", Toast.LENGTH_SHORT).show(); // 提示用户输入
            }
        });




        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.map;
        campusBar = binding.campusBar;
        mapView.setImageResource(R.drawable.map_sip);
        mapView.setBounds(sipBuildings);

        campusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (seekBar.getProgress()) {
                    case 0:
                        mapView.setImageResource(R.drawable.map_sip);
                        mapView.setBounds(sipBuildings);
                        break;
                    case 1:
                        mapView.setImageResource(R.drawable.map_tc);
                        mapView.setBounds(tcBuildings);
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

        mapView.setMarkerDraw(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gps));


        mapView.setOnBoundClickListener(building -> {
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
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    gpsTextView.setText("Current GPS: " + latitude + ", " + longitude);
                    gpsPosition = transformGpsToImageCoord(longitude, latitude);
                    if (gpsPosition[0] >= 0 && gpsPosition[0] <= 1 && gpsPosition[0] >= 0 && gpsPosition[0] <= 1) {
                        if (!mapView.isGpsDrawn()) {
                            mapView.setGpsDraw(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gps));
                        }
                    } else {
                        mapView.cancelGpsDraw();
                    }
                    mapView.setGpsPosition(gpsPosition[0], gpsPosition[1]);
                }
            }
        };
        startLocationUpdates();
        return root;
    }


    // 更新 Spinner 的方法
    private void updateTeacherSpinner(List<String> results) {
        // 创建 ArrayAdapter，并指定显示格式
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),  // 上下文
                android.R.layout.simple_spinner_item,  // 单个条目的布局
                results);  // 数据源

        // 设置下拉框的布局样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 将适配器设置给 Spinner
        teacherSpinner.setAdapter(adapter);
    }



    public String getLocationName() {
        String s = mapView.getBound(gpsPosition[0], gpsPosition[1]);
        if (gpsPosition[0] >= 0 && gpsPosition[0] <= 1 && gpsPosition[0] >= 0 && gpsPosition[0] <= 1) {
            if (s.isEmpty()) {
                return "SIP campus";
            } else {
                return "SIP campus, " + s + " building";
            }
        }
        return "Not in school.";
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


//防止数据内存泄露哈
        if (database != null) {
            database.close();
        }
    }

    // 在这里做数据库相关的操作，比如模糊查询
    private List<String> fuzzySearchName(String query) {
        List<String> teacherNames = new ArrayList<>();
        Cursor cursor = null;

        try {
            String sqlQuery = "SELECT Name FROM Teachers_Basic_Information WHERE Name LIKE ?";
            cursor = database.rawQuery(sqlQuery, new String[]{"%" + query + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("Name");
                if (columnIndex != -1) { // 检查列索引是否存在
                    do {
                        String teacherName = cursor.getString(columnIndex);
                        teacherNames.add(teacherName);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e("MapFragment", "Error during fuzzy search", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return teacherNames;
    }
}