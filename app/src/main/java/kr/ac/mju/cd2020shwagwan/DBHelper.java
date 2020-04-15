package kr.ac.mju.cd2020shwagwan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private volatile static DBHelper _instance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hwagwan.db";

    /* 싱글톤 패턴 적용 */
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

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성

        db.execSQL("CREATE TABLE cosmetics(cID INTEGER PRIMARY KEY AUTOINCREMENT, brand TEXT,  name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS cosmetics");

        // 테이블 삭제후 다시 생성하기 위함
        onCreate(db);
    }
}