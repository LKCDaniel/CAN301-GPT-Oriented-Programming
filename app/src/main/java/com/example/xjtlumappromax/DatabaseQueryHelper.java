package com.example.xjtlumappromax;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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



    //2024/12/1 创建位置和名字的映射
    // 查询某个表，并将"location"列和"teacher"列映射成Map
    public Map<String, String> mapLocationTeacher(String tableName, String locationColumn, String teacherColumn) {
        Map<String, String> locationTeacherMap = new HashMap<>();
        Cursor cursor = null;

        try {
            // 执行查询操作
            String query = "SELECT " + locationColumn + ", " + teacherColumn + " FROM " + tableName;
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int locationIndex = cursor.getColumnIndex(locationColumn);
                int teacherIndex = cursor.getColumnIndex(teacherColumn);

                // 检查列索引是否有效
                if (locationIndex == -1 || teacherIndex == -1) {
                    Log.e(TAG, "One or both columns '" + locationColumn + "' or '" + teacherColumn + "' not found in table '" + tableName + "'");
                } else {
                    do {
                        // 获取每一行的 location 和 teacher
                        String location = cursor.getString(locationIndex);
                        String teacher = cursor.getString(teacherIndex);

                        // 将其添加到 Map 中
                        locationTeacherMap.put(location, teacher);
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

        return locationTeacherMap;  // 返回映射关系
    }




}
