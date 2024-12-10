package com.example.xjtlumappromax;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.xjtlumappromax.ui.friends.FriendsViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseQueryHelper {

    private static final String TAG = "DatabaseQueryHelper";
    private SQLiteDatabase db;

    // 构造方法接收 SQLiteDatabase 参数
    public DatabaseQueryHelper(SQLiteDatabase db) {
        this.db = db;
    }

    // 查询某个表的所有数据
    public List<String> getAllDataFromTable(String tableName, String columnName) {
        List<String> dataList = new ArrayList<>();
        Cursor cursor = null;

        try {
            // 执行查询操作
            String query = "SELECT " + columnName + " FROM " + tableName;
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(columnName);

                // 检查列索引是否有效
                if (columnIndex == -1) {
                    Log.e(TAG, "Column '" + columnName + "' not found in table '" + tableName + "'");
                } else {
                    do {
                        // 获取列值
                        String data = cursor.getString(columnIndex);
                        dataList.add(data);  // 添加到列表中
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying database", e);
        } finally {
            if (cursor != null) {
                cursor.close();  // 关闭游标
            }
        }

        return dataList;  // 返回查询结果
    }

//    public List<FriendsViewModel.Friend> getTopFriends() {
//        List<FriendsViewModel.Friend> topFriends = new ArrayList<>();
//        Cursor cursor = null;
//
//        try {
//            // 获取前三位
//            String query = "SELECT id, name, ranking FROM friends_table ORDER BY ranking DESC LIMIT 3";
//            cursor = db.rawQuery(query, null);
//
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
//                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
//                    @SuppressLint("Range") int ranking = cursor.getInt(cursor.getColumnIndex("ranking"));
//                    topFriends.add(new FriendsViewModel.Friend(name, id, ranking));
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error querying top friends", e);
//        } finally {
//            if (cursor != null) {
//                cursor.close(); // Close the cursor
//            }
//        }
//
//        return topFriends;
//    }
//


    public static class Friend {
        public String name;
        public int score;
        public int rank;

        public Friend(String name, int score, int rank, int logoResId) {
            this.name = name;
            this.score = score;
            this.rank = rank;
        }
    }
}
