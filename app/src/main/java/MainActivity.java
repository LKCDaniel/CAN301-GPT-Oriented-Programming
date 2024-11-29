import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xjtlumappromax.DatabaseHelper;
import com.example.xjtlumappromax.DatabaseQueryHelper;
import com.example.xjtlumappromax.R;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

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
