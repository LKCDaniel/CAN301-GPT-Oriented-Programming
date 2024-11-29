package com.example.xjtlumappromax;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CAN301.sqlite";  // 数据库文件名
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 在这里创建表，如果需要的话
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 处理数据库升级的逻辑
    }

    // 检查数据库是否已存在
    public boolean checkDatabase() {
        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    // 创建数据库，如果不存在则复制它
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (dbExist) {
            Log.d("DatabaseHelper", "Database already exists.");
        } else {
            this.getReadableDatabase();  // 创建数据库文件
            try {
                copyDatabase();  // 复制数据库
                Log.d("DatabaseHelper", "Database copied from assets.");
            } catch (IOException e) {
                Log.e("DatabaseHelper", "Error copying database", e);
                throw new IOException("Error copying database", e);
            }
        }
    }

    // 复制数据库文件到应用的数据库目录
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

    // 打开数据库
    public SQLiteDatabase openDatabase() throws SQLException {
        String dbPath = myContext.getDatabasePath(DB_NAME).getPath();
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error opening database", e);
            throw new SQLException("Error opening database", e);
        }
        return db;
    }

    // 关闭数据库
    @Override
    public synchronized void close() {
        if (myDatabase != null) {
            myDatabase.close();
        }
        super.close();
    }
}
