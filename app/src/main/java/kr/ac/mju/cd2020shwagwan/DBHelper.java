package kr.ac.mju.cd2020shwagwan;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private volatile static DBHelper _instance = null;
    DBHelper dbHelper;
    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hwagwan.db";

    public static final String TABLE_COSMETIC = "cosmetics";
    public static final String TABLE_BARCODE_INFO = "barcodeinfos";

    /* 싱글톤 패턴 적용  */
    public static DBHelper getInstance(Context context) {
        if (_instance == null) {
            synchronized (DBHelper.class) {
                if (_instance == null) {
                    _instance = new DBHelper(context);
                }
            }
        }

        return _instance;
    }
    /* df */
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COSMETIC);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARCODE_INFO);

            db.execSQL("CREATE TABLE "+TABLE_COSMETIC+"(cID INTEGER PRIMARY KEY AUTOINCREMENT, brandName TEXT, productName TEXT, dtOpen TEXT, dtExp TEXT," +
                    " kind TEXT, alarm INTEGER, volume TEXT, additionalContent Text);");

            db.execSQL("CREATE TABLE "+TABLE_BARCODE_INFO+"(bID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "bcdId TEXT, bcdBrand TEXT, bcdProduct TEXT, bcdVolume TEXT);");
        } catch (SQLException e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COSMETIC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARCODE_INFO);
        // 테이블 삭제후 다시 생성하기 위함
        onCreate(db);


    }

   /* public BarcodeInfo open() throws SQLException {
        DBHelper =  DBHelper.this;
        db = DBHelper.getReadableDatabase();
        return this;
    }
*/


}