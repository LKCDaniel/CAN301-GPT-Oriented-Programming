package com.example.xjtlumappromax.ui.map;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.SeekBar;

import com.example.xjtlumappromax.DatabaseHelper;
import com.example.xjtlumappromax.InteractiveImageView;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.databinding.ActivityBuildingBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildingActivity extends AppCompatActivity {

    private static final String TAG = "BuildingActivity";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private ActivityBuildingBinding binding;
    private final Map<String, float[]> SC_5F = Map.of(
            "540", new float[]{0, 0, 0, 0},
            "523", new float[]{0.2f, 0.2f, 0.5f, 0.5f},
            "567", new float[]{0, 0, 0, 0}
    ), SC_4F = Map.of(
            "440", new float[]{0, 0, 0, 0},
            "423", new float[]{0.2f, 0.2f, 0.5f, 0.5f},
            "467", new float[]{0, 0, 0, 0}
    ), SA_3F = Map.of(
            "340", new float[]{0, 0, 0, 0},
            "323", new float[]{0.2f, 0.2f, 0.5f, 0.5f},
            "367", new float[]{0, 0, 0, 0}
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBuildingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        String building = getIntent().getStringExtra("building");
        InteractiveImageView map = binding.floorMap;
        SeekBar floorBar = binding.floorBar;
        floorBar.setProgress(0);
        map.setImageResource(R.drawable.sd5);
        map.setBounds(SC_5F);

        switch (building) {
            case "SA":
                floorBar.setMax(4);
                map.setImageResource(R.drawable.image_test); // floor zero, same for all
                break;
            case "SB":
                floorBar.setMax(4);
                break;
            case "SC":
                floorBar.setMax(4);
                break;
            case "SD":
                floorBar.setMax(4);
                break;
        }

        floorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (building) {
                    case "SA":
                        break;
                    case "SB":
                        switch (seekBar.getProgress()) {
                            case 0:
                                map.setImageResource(R.drawable.sd5);
                                map.setBounds(SC_5F);
                                break;
                            case 1:
                                map.setImageResource(R.drawable.image_test);
                                map.setBounds(SC_4F);
                                break;
                            case 2:
                                map.setImageResource(R.drawable.map_sip);
                                map.setBounds(SA_3F);
                                break;
                        }
                        break;
                    case "SC":
                        // do nothing
                        break;
                    case "SD":
                        switch (seekBar.getProgress()) {
                            case 0:
                                map.setImageResource(R.drawable.buttom_letgo);
                                map.setBounds(SC_5F);
                                break;
                            case 1:
                                map.setImageResource(R.drawable.ic_launcher_background);
                                map.setBounds(SC_4F);
                                break;
                            case 2:
                                map.setImageResource(R.drawable.rank);
                                map.setBounds(SA_3F);
                                break;
                        }
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

        map.setOnBoundClickListener(room -> {
            Log.i("BuildingActivity", "Room " + room + " clicked");
            // do nothing

            List<String> columnsToFetch = Arrays.asList
                    ("Name", "Position", "Email", "Photo URL", "Scholar URL");
            Map<String, String> teacherInfo = fetchDataByCondition
                    ("Teachers_Basic_Information", columnsToFetch, "Location", room);

            String name = teacherInfo.get("Nmae");
            String email = teacherInfo.get("Position");
            String photo = teacherInfo.get("Email");
            String scholar = teacherInfo.get("Photo URL");
            String details = teacherInfo.get("Scholar URL");


        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }





    /**
     * 通用查询方法，返回指定表和列的数据，基于单个条件。
     *
     * @param tableName       要查询的表名。
     * @param columns         要查询的列的列表。
     * @param conditionColumn 用于条件判断的列名。
     * @param conditionValue  条件列的值。
     * @return 包含查询结果的映射，键为列名，值为对应的数据。
     */
    public Map<String, String> fetchDataByCondition(String tableName, List<String> columns, String conditionColumn, String conditionValue) {
        Map<String, String> result = new HashMap<>();
        Cursor cursor = null;
        try {
            // 构建查询所需的列名字符串
            String columnsList = String.join(", ", columns);
            // 构建查询语句
            String query = "SELECT " + columnsList + " FROM " + tableName + " WHERE " + conditionColumn + " = ?";
            cursor = database.rawQuery(query, new String[]{conditionValue});

            if (cursor != null && cursor.moveToFirst()) {
                for (String column : columns) {
                    int index = cursor.getColumnIndex(column);
                    if (index != -1) {
                        result.put(column, cursor.getString(index));
                    } else {
                        Log.e(TAG, "Column '" + column + "' not found in table '" + tableName + "'");
                        result.put(column, "Column not found");
                    }
                }
            } else {
                Log.d(TAG, "No data found for " + conditionColumn + " = " + conditionValue);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying database for " + conditionColumn + " = " + conditionValue, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }


}