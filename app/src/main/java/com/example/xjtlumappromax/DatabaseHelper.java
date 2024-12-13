package com.example.xjtlumappromax;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "CAN301.sqlite";  // database name
    private static DatabaseHelper instance;  //Only one instance
    private static SQLiteDatabase database;  // Global database instance
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }


    //synchronization
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }


    public synchronized SQLiteDatabase getDatabase() throws IOException {
        if (database == null || !database.isOpen()) {
            createDatabase();
            database = openDatabase();
        }
        return database;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //Database Update
    }

    // check if database already exists
    public boolean checkDatabase() {
        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    // Create the database and copy it if it does not exist
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (dbExist) {
            Log.d("DatabaseHelper", "Database already exists.");
        } else {
            this.getReadableDatabase();
            try {
                copyDatabase();
                Log.d("DatabaseHelper", "Database copied from assets.");
            } catch (IOException e) {
                Log.e("DatabaseHelper", "Error copying database", e);
                throw new IOException("Error copying database", e);
            }
        }
    }

    // Copy the database file to the application's database directory
    private void copyDatabase() throws IOException {
        AssetManager assetManager = myContext.getAssets();
        InputStream myInput = assetManager.open(DB_NAME);
        String outFileName = myContext.getDatabasePath(DB_NAME).getPath();
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    public SQLiteDatabase openDatabase() throws SQLException {
        String dbPath = myContext.getDatabasePath(DB_NAME).getPath();
        Log.i("dbPath of database is", dbPath);
        SQLiteDatabase db;
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error opening database", e);
            throw new SQLException("Error opening database", e);
        }
        return db;
    }


    // prevent lacking of data
    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }
}
