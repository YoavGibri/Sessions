package com.yoavgibri.myincome.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Yoav on 04/08/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public final static int DB_VERSION = 11;
    public final static String DATABASE_NAME = "MyIncome";
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //JOBS TABLE:
        String sqlJobs = "CREATE TABLE " + JobsDBHelper.TABLE_JOBS +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                JobsDBHelper.JOB + " INTEGER, " +
                JobsDBHelper.CLIENT_NAME + " TEXT, " +
                JobsDBHelper.CLIENT_CALENDAR_NAME + " TEXT, " +
                JobsDBHelper.JOB_TYPE + " INTEGER, " +
                JobsDBHelper.PAYMENT_AMOUNT + " INTEGER, " +
                JobsDBHelper.JOB_TIME + " INTEGER, " +
                JobsDBHelper.COMMENT + " TEXT, " +
                JobsDBHelper.IS_PAID + " INTEGER, " +
                JobsDBHelper.INSERT_TIME + " INTEGER)";
        Log.d(TAG, "onCreate: " + sqlJobs);
        db.execSQL(sqlJobs);

                //CLIENTS TABLE:
                String sqlClients = "CREATE TABLE " + ClientsDBHelper.TABLE_CLIENTS +
                        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ClientsDBHelper.CLIENT_ID + " INTEGER, " +
                ClientsDBHelper.CLIENT_NAME + " TEXT)";
        Log.d(TAG, "onCreate: " + sqlClients);
        db.execSQL(sqlClients);

        //JOB TYPES TABLE:
        String sqlJobTypes = "CREATE TABLE " + JobTypesDBHelper.TABLE_JOB_TYPES +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JobTypesDBHelper.JOBTYPE_ID + " INTEGER, "
                + JobTypesDBHelper.JOBTYPE_NAME + " TEXT)";
        Log.d(TAG, "onCreate: " + sqlJobTypes);
        db.execSQL(sqlJobTypes);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + JobsDBHelper.TABLE_JOBS);
        db.execSQL("DROP TABLE IF EXISTS " + ClientsDBHelper.TABLE_CLIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + JobTypesDBHelper.TABLE_JOB_TYPES);

        onCreate(db);
    }

//    public Cursor getAllJobsCursor() {
//
////        String sql = "SELECT "
////
////        Cursor cursor = db.rawQuery()
////        return cursor;
//    }

}
