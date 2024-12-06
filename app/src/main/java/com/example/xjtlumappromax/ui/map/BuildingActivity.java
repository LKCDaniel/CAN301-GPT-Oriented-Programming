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
    private Map<String, float[]> SD_5F, SD_4F, SD_3F, SD_2F, SD_1F;

    public BuildingActivity() {
        SD_5F = new HashMap<>();
        SD_5F.put("528", new float[]{0.03829316f, 0.006989742f, 0.47643355f, 0.1507825f});
        SD_5F.put("536", new float[]{0.03829316f, 0.1507825f, 0.47643355f, 0.29461312f});
        SD_5F.put("540E", new float[]{0.03829316f, 0.29461312f, 0.62395895f, 0.37168148f});
        SD_5F.put("540W", new float[]{0.03829316f, 0.37168148f, 0.62395895f, 0.4451503f});
        SD_5F.put("546", new float[]{0.03829316f, 0.4451503f, 0.62395895f, 0.597241f});
        SD_5F.put("554", new float[]{0.03829316f, 0.597241f, 0.62395895f, 0.7519082f});

        SD_5F.put("523", new float[]{0.7348369f, 0.028538045f, 0.84798205f, 0.080666974f});
        SD_5F.put("525", new float[]{0.7348369f, 0.080666974f, 0.84798205f, 0.1098245f});

        SD_5F.put("529", new float[]{0.7348369f, 0.14833882f, 0.93850976f, 0.1854425f});
        SD_5F.put("531", new float[]{0.7348369f, 0.1854425f, 0.93850976f, 0.22166616f});
        SD_5F.put("533", new float[]{0.7348369f, 0.22166616f, 0.93850976f, 0.25664744f});
        SD_5F.put("535", new float[]{0.7348369f, 0.25664744f, 0.93850976f, 0.29446292f});
        SD_5F.put("537", new float[]{0.7348369f, 0.29446292f, 0.93850976f, 0.3313854f});
        SD_5F.put("539", new float[]{0.7348369f, 0.3313854f, 0.93850976f, 0.3663796f});
        SD_5F.put("541", new float[]{0.7348369f, 0.3663796f, 0.93850976f, 0.40064904f});
        SD_5F.put("543", new float[]{0.7348369f, 0.40064904f, 0.93850976f, 0.4363421f});
        SD_5F.put("545", new float[]{0.7348369f, 0.4363421f, 0.93850976f, 0.47840244f});
        SD_5F.put("547", new float[]{0.7348369f, 0.47840244f, 0.93850976f, 0.5139143f});

        SD_5F.put("551", new float[]{0.7348369f, 0.53689253f, 0.93850976f, 0.59107935f});
        SD_5F.put("553", new float[]{0.7348369f, 0.59107935f, 0.93850976f, 0.63039523f});
        SD_5F.put("555", new float[]{0.7348369f, 0.63039523f, 0.93850976f, 0.6687467f});
        SD_5F.put("557", new float[]{0.7348369f, 0.6687467f, 0.93850976f, 0.709586f});
        SD_5F.put("559", new float[]{0.7348369f, 0.709586f, 0.93850976f, 0.74698704f});
        SD_5F.put("561", new float[]{0.7348369f, 0.74698704f, 0.93850976f, 0.7872533f});
        SD_5F.put("563", new float[]{0.7348369f, 0.7872533f, 0.93850976f, 0.8263595f});
        SD_5F.put("565", new float[]{0.7348369f, 0.8263595f, 0.93850976f, 0.86472493f});
        SD_5F.put("567", new float[]{0.7348369f, 0.86472493f, 0.93850976f, 0.905746f});
        SD_5F.put("573", new float[]{0.6201988f, 0.9060625f, 0.93850976f, 1.0000576f});
    }

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


/*        switch (building) {
            case "SD":
                floorBar.setMax(4);
                map.setImageResource(R.drawable.sd5);
                map.setBounds(SD_5F);
                break;
            default:
                map.setImageResource(R.drawable.not_yet_complete);
        }*/

        floorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (building) {
                    case "SD":
                        switch (seekBar.getProgress()) {
                            case 0:
                                map.setImageResource(R.drawable.sd4);
                                map.setBounds(SD_1F);
                                break;
                            case 1:
                                map.setImageResource(R.drawable.sd4);
                                map.setBounds(SD_2F);
                                break;
                            case 2:
                                map.setImageResource(R.drawable.sd4);
                                map.setBounds(SD_3F);
                                break;
                            case 3:
                                map.setImageResource(R.drawable.sd4);
                                map.setBounds(SD_4F);
                                break;
                            case 4:
                                map.setImageResource(R.drawable.sd5);
                                map.setBounds(SD_5F);
                                break;
                        }

                    case "SA":
                        break;
                }
                Log.i("BuildingActivity", "Floor " + seekBar.getProgress() + " selected");
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

            String name = teacherInfo.get("Name");
            String position = teacherInfo.get("Position");
            String email = teacherInfo.get("Email");
            String photo = teacherInfo.get("Photo URL");
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