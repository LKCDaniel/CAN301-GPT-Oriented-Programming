package com.example.xjtlumappromax.ui.map;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.SeekBar;

import com.example.xjtlumappromax.DatabaseHelper;
import com.example.xjtlumappromax.InteractiveImageView;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.databinding.ActivityBuildingBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildingActivity extends AppCompatActivity {

    private static final String TAG = "BuildingActivity";
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
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


        SD_4F = new HashMap<>();
        SD_4F.put("428", new float[]{0.035877228f, 0.0035337165f, 0.63024354f, 0.1476511f});
        SD_4F.put("436", new float[]{0.035877228f, 0.1476511f, 0.49121776f, 0.2917685f});
        SD_4F.put("440", new float[]{0.035877228f, 0.2917685f, 0.63024354f, 0.4401071f});
        SD_4F.put("446E", new float[]{0.035877228f, 0.4401071f, 0.63024354f, 0.5170544f});
        SD_4F.put("446W", new float[]{0.035877228f, 0.5170544f, 0.63024354f, 0.59494513f});
        SD_4F.put("454", new float[]{0.035877228f, 0.59494513f, 0.63024354f, 0.749964f});

        SD_4F.put("421", new float[]{0.75058746f, 0.004686064f, 0.8654696f, 0.04596208f});
        SD_4F.put("423", new float[]{0.75058746f, 0.04596208f, 0.8654696f, 0.077031545f});
        SD_4F.put("425", new float[]{0.75058746f, 0.077031545f, 0.8654696f, 0.11138014f});

        SD_4F.put("429", new float[]{0.75058746f, 0.14858863f, 0.96365225f, 0.1855875f});
        SD_4F.put("431", new float[]{0.75058746f, 0.1855875f, 0.96365225f, 0.22360453f});
        SD_4F.put("433", new float[]{0.75058746f, 0.22360453f, 0.96365225f, 0.26000446f});
        SD_4F.put("435", new float[]{0.75058746f, 0.26000446f, 0.96365225f, 0.2980215f});
        SD_4F.put("437", new float[]{0.75058746f, 0.2980215f, 0.96365225f, 0.3342118f});
        SD_4F.put("439", new float[]{0.75058746f, 0.3342118f, 0.96365225f, 0.36956358f});
        SD_4F.put("441", new float[]{0.75058746f, 0.36956358f, 0.96365225f, 0.40043843f});
        SD_4F.put("443", new float[]{0.75058746f, 0.40043843f, 0.96365225f, 0.4799613f});
        SD_4F.put("447", new float[]{0.75058746f, 0.4799613f, 0.96365225f, 0.5145045f});

        SD_4F.put("451", new float[]{0.75058746f, 0.53576654f, 0.96365225f, 0.5915867f});
        SD_4F.put("453", new float[]{0.75058746f, 0.5915867f, 0.96365225f, 0.62960374f});
        SD_4F.put("455", new float[]{0.75058746f, 0.62960374f, 0.96365225f, 0.6676208f});
        SD_4F.put("457", new float[]{0.75058746f, 0.6676208f, 0.96365225f, 0.7091266f});
        SD_4F.put("459", new float[]{0.75058746f, 0.7091266f, 0.96365225f, 0.7461255f});
        SD_4F.put("461", new float[]{0.75058746f, 0.7461255f, 0.96365225f, 0.7851757f});
        SD_4F.put("463", new float[]{0.75058746f, 0.7851757f, 0.96365225f, 0.825858f});
        SD_4F.put("465", new float[]{0.75058746f, 0.825858f, 0.96365225f, 0.86550707f});
        SD_4F.put("467", new float[]{0.75058746f, 0.86550707f, 0.96365225f, 0.90618926f});
        SD_4F.put("473", new float[]{0.6316989f, 0.90618926f, 0.96365225f, 1.000213f});


        SD_3F = new HashMap<>();
        SD_3F.put("319", new float[]{0.05535408f, 0.002168933f, 0.9227651f, 0.0760938f});

        SD_3F.put("334A", new float[]{0.05535408f, 0.1437638f, 0.22238801f, 0.1765962f});
        SD_3F.put("334B", new float[]{0.05535408f, 0.1765962f, 0.22238801f, 0.21163401f});
        SD_3F.put("334C", new float[]{0.05535408f, 0.21163401f, 0.22238801f, 0.24599901f});
        SD_3F.put("334D", new float[]{0.05535408f, 0.24599901f, 0.22238801f, 0.28665632f});
        SD_3F.put("334E", new float[]{0.05535408f, 0.28665632f, 0.22238801f, 0.3261175f});
        SD_3F.put("334F", new float[]{0.05535408f, 0.3261175f, 0.22238801f, 0.36422047f});

        SD_3F.put("334G", new float[]{0.3089807f, 0.1437638f, 0.4807872f, 0.1765962f});
        SD_3F.put("334I", new float[]{0.3089807f, 0.1765962f, 0.4807872f, 0.21163401f});
        SD_3F.put("334J", new float[]{0.3089807f, 0.21163401f, 0.4807872f, 0.24599901f});

        SD_3F.put("334K", new float[]{0.3089807f, 0.28665632f, 0.4807872f, 0.3261175f});
        SD_3F.put("334L", new float[]{0.3089807f, 0.3261175f, 0.4807872f, 0.36422047f});

        SD_3F.put("334M", new float[]{0.4807872f, 0.31995007f, 0.61032295f, 0.36451986f});
        SD_3F.put("340", new float[]{0.4807872f, 0.36451986f, 0.61032295f, 0.40110272f});
        SD_3F.put("342", new float[]{0.4807872f, 0.40110272f, 0.61032295f, 0.43682587f});
        SD_3F.put("344", new float[]{0.4807872f, 0.43682587f, 0.61032295f, 0.47288543f});
        SD_3F.put("346", new float[]{0.4807872f, 0.47288543f, 0.61032295f, 0.512359f});
        SD_3F.put("348", new float[]{0.4807872f, 0.512359f, 0.61032295f, 0.5516582f});
        SD_3F.put("350", new float[]{0.4807872f, 0.5516582f, 0.61032295f, 0.5911194f});

        SD_3F.put("352", new float[]{0.4807872f, 0.5911194f, 0.61032295f, 0.6263317f});
        SD_3F.put("354", new float[]{0.05535408f, 0.5911194f, 0.4807872f, 0.74711996f});
        SD_3F.put("356", new float[]{0.4807872f, 0.66869605f, 0.61032295f, 0.7052664f});

        SD_3F.put("357", new float[]{0.7298329f, 0.58716077f, 0.92459345f, 0.6258212f});
        SD_3F.put("359", new float[]{0.7298329f, 0.6258212f, 0.92459345f, 0.6639007f});
        SD_3F.put("361", new float[]{0.7298329f, 0.6639007f, 0.92459345f, 0.7055383f});
        SD_3F.put("363", new float[]{0.7298329f, 0.7055383f, 0.92459345f, 0.742819f});
        SD_3F.put("365", new float[]{0.7298329f, 0.742819f, 0.92459345f, 0.78306246f});
        SD_3F.put("367", new float[]{0.7298329f, 0.78306246f, 0.92459345f, 0.82172275f});
        SD_3F.put("369", new float[]{0.7298329f, 0.82172275f, 0.92459345f, 0.86158866f});
        SD_3F.put("371", new float[]{0.7298329f, 0.86158866f, 0.92459345f, 0.9022387f});
        SD_3F.put("373", new float[]{0.61407036f, 0.9022387f, 0.92459345f, 0.99681294f});


        SD_2F = new HashMap<>();
        SD_2F.put("102", new float[]{0.005117832f, 0.13199803f, 0.3408165f, 0.344219f});
        SD_2F.put("214", new float[]{0.3408165f, 0.15386197f, 0.5371981f, 0.30920446f});
        SD_2F.put("220", new float[]{0.5371981f, 0.15386197f, 0.7328791f, 0.30920446f});
        SD_2F.put("240A", new float[]{0.3408165f, 0.30920446f, 0.5371981f, 0.344219f});

        SD_2F.put("219", new float[]{0.5173823f, 0, 0.732902f, 0.07460401f});
        SD_2F.put("223", new float[]{0.732902f, 0, 0.9590543f, 0.07460401f});

        SD_2F.put("240", new float[]{0.73244923f, 0.36513597f, 0.7992493f, 0.40046757f});
        SD_2F.put("242", new float[]{0.73244923f, 0.40046757f, 0.7992493f, 0.43675387f});
        SD_2F.put("244", new float[]{0.73244923f, 0.43675387f, 0.7992493f, 0.47383732f});
        SD_2F.put("246", new float[]{0.73244923f, 0.47383732f, 0.7992493f, 0.51267254f});
        SD_2F.put("248", new float[]{0.73244923f, 0.51267254f, 0.7992493f, 0.5512322f});
        SD_2F.put("250", new float[]{0.73244923f, 0.5512322f, 0.7992493f, 0.589664f});

        SD_2F.put("254", new float[]{0.5185223f, 0.5917131f, 0.73073167f, 0.7454386f});
        SD_2F.put("256", new float[]{0.73073167f, 0.6430234f, 0.8019067f, 0.70253f});

        SD_2F.put("255", new float[]{0.8600365f, 0.5878201f, 0.96029097f, 0.66329306f});
        SD_2F.put("259", new float[]{0.8600365f, 0.66329306f, 0.96029097f, 0.7335861f});
        SD_2F.put("263", new float[]{0.8600365f, 0.7335861f, 0.96029097f, 0.8038633f});

        SD_2F.put("267", new float[]{0.8600365f, 0.8308213f, 0.96029097f, 0.90154064f});
        SD_2F.put("273", new float[]{0.8036228f, 0.90154064f, 0.96029097f, 0.997702f});


        SD_1F = new HashMap<>();
        SD_1F.put("102", new float[]{0.0050785732f, 0.07900052f, 0.39329934f, 0.36519983f});
        SD_1F.put("114", new float[]{0.39329934f, 0.109042056f, 0.6230075f, 0.31722826f});
        SD_1F.put("120", new float[]{0.6230075f, 0.109042056f, 0.8522333f, 0.31722826f});
        SD_1F.put("140A", new float[]{0.39329934f, 0.31722826f, 0.6230075f, 0.36519983f});

        SD_1F.put("154", new float[]{0.59814f, 0.5247025f, 0.9863446f, 0.78246963f});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuildingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            dbHelper = DatabaseHelper.getInstance(this);
            database = dbHelper.getDatabase(); // Gets a globally shared database instance
            Log.i(TAG, "Database initialized successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error initializing database", e);
            return;
        }

        if (database == null) {
            Log.e(TAG, "Database is null after initialization.");
            return;
        }


        binding = ActivityBuildingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

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

        String building = getIntent().getStringExtra("building");
        InteractiveImageView map = binding.floorMap;
        SeekBar floorBar = binding.floorBar;

        floorBar.setProgress(0);
        switch (building) {
            case "SD":
                floorBar.setMax(4);
                map.setImageResource(R.drawable.sd1);
                map.setBounds(SD_1F);
                break;
            default:
                map.setImageResource(R.drawable.not_yet_complete);
        }

        floorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (building) {
                    case "SD":
                        switch (seekBar.getProgress()) {
                            case 0:
                                map.setImageResource(R.drawable.sd1);
                                map.setBounds(SD_1F);
                                break;
                            case 1:
                                map.setImageResource(R.drawable.sd2);
                                map.setBounds(SD_2F);
                                break;
                            case 2:
                                map.setImageResource(R.drawable.sd3);
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
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        map.setOnBoundClickListener(room -> {
            Log.i(TAG, "Room " + room + " clicked");

            List<String> columnsToFetch = Arrays.asList("Name", "Position", "Email", "Photo URL", "Scholar URL");
            Map<String, String> teacherInfo = fetchDataByCondition
                    ("Teachers_Basic_Information",
                            columnsToFetch,
                            "Location",
                            "SD" + room);

            String name = teacherInfo.get("Name");
            String position = teacherInfo.get("Position");
            String email = teacherInfo.get("Email");
            String photo = teacherInfo.get("Photo URL");
            String details = teacherInfo.get("Scholar URL");

            Log.i(TAG, "Name: " + name);
            Log.i(TAG, "Position: " + position);
            Log.i(TAG, "Email: " + email);
            Log.i(TAG, "Photo URL: " + photo);
            Log.i(TAG, "Scholar URL: " + details);


            if (name == null || position == null || email == null || photo == null || details == null) {
                Intent intent = new Intent(BuildingActivity.this, TeacherBasicInfoActivity.class);
                intent.putExtra("name", "XJTLU Member");
                intent.putExtra("position", "N/A");
                intent.putExtra("email", "N/A");
                intent.putExtra("photo", "N/A");
                intent.putExtra("details", "https://xjtlu.edu.cn/");
                startActivity(intent);
            } else {
                Intent intent = new Intent(BuildingActivity.this, TeacherBasicInfoActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("position", position);
                intent.putExtra("email", email);
                intent.putExtra("photo", photo);
                intent.putExtra("details", details);
                startActivity(intent);
            }


        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }


    /**
     * Generic query method that returns data for
     * specified tables and columns, based on a single condition.
     *
     * @param tableName       Name of the table to be queried.
     * @param columns         A list of columns to query.
     * @param conditionColumn Column name used for conditional judgment.
     * @param conditionValue  The value of the condition column.
     * @return A map that contains query results, with the key being the column name and the value being the corresponding data.
     */
    public Map<String, String> fetchDataByCondition(String tableName, List<String> columns, String conditionColumn, String conditionValue) {
        Map<String, String> result = new HashMap<>();
        Cursor cursor = null;
        try {
            List<String> escapedColumns = new ArrayList<>();
            for (String col : columns) {
                escapedColumns.add("\"" + col + "\"");
            }
            String columnsList = String.join(", ", escapedColumns);

            String escapedConditionColumn = "\"" + conditionColumn + "\"";

            String escapedTableName = "\"" + tableName + "\"";

            String query = "SELECT " + columnsList + " FROM " + escapedTableName + " WHERE " + escapedConditionColumn + " = ?";
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