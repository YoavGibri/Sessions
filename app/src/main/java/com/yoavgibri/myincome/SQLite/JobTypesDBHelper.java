package com.yoavgibri.myincome.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Yoav on 04/08/16.
 */
public class JobTypesDBHelper{

    public final static String TABLE_JOB_TYPES = "jobtypes";
    public final static String JOBTYPE_NAME = "type";
    public final static String JOBTYPE_ID = "jobType";
    private static final String TAG = "JobTypesDBHelper";
    private final DBHelper dbHelper;
    private SQLiteDatabase db;


    public JobTypesDBHelper(Context context, DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        if (!getAllJobTypesCursor().moveToFirst()){
            insert("Massage at home");
            insert("Massage at the clinic");
            insert("Event");
        }
    }


    public void insert(String jobTypeName) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(JOBTYPE_NAME, jobTypeName);
        db.insert(TABLE_JOB_TYPES, null, values);

        db.close();
    }

    public Cursor getAllJobTypesCursor() {
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_JOB_TYPES;
        return db.rawQuery(sql, null);
    }




//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//    }

}
