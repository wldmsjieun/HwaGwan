package kr.ac.mju.cd2020shwagwan;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    boolean checkDB;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Object[] args;
    String mSql = "INSERT INTO "+DBHelper.TABLE_BARCODE_INFO+"(bcdId, bcdBrand, bcdProduct, bcdVolume) VALUES(?,?,?,?)";
    String TABLE_BARCODE_INFO = "barcodeinfos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = findViewById(R.id.nav_view);


        dbHelper = DBHelper.getInstance(this);
        db = dbHelper.getReadableDatabase();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



        Workbook workbook = null;
        Sheet sheet = null;

        try {
            String sql = "SELECT bID, bcdId, bcdBrand, bcdProduct, bcdVolume FROM barcodeinfos";
            Cursor cursor = db.rawQuery(sql, null);

            checkDB = true;

            while (cursor.moveToNext()) {
                if ((cursor.getString(0)) != null) {
                    checkDB = false;
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("barcode_info.xls");
            if(is != null){
                Log.d(TAG, "xls open");
            }

            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);

                if (sheet != null) {

                    if (checkDB) {
                        int nMaxColumn = 4;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(4).length - 1;
                        Log.d(TAG, "nRowEndIndex : " + String.valueOf(nRowEndIndex));

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String bInfo = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String bBrand = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                            String bName = sheet.getCell(nColumnStartIndex + 2, nRow).getContents();
                            String bVolume = sheet.getCell(nColumnStartIndex + 3, nRow).getContents();
                            Log.d(TAG, "바코드 정보 " + bInfo + " : " + bName);
                            args = new Object[]{bInfo, bBrand, bName, bVolume};
                            db.execSQL(mSql, args);
                        }
                        db.close();
                    }
                } else {
                    Log.d(TAG, "바코드 정보 - sheet is null");
                }
            } else {
                Log.d(TAG, "바코드 정보 - workbook is null");
            }
        } catch (Exception e) {
            Log.d(TAG, "바코드 정보 error "+e.getMessage());
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

}