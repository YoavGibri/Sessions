package com.yoavgibri.myincome.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoavgibri.myincome.Models.Session;

/**
 * Created by Yoav on 16/07/16.
 */
public class JobsDBHelper {

    public final static String TABLE_JOBS = "jobs";
    public static final String JOB = "job";
    public final static String CLIENT_NAME = "clientName";
    public final static String CLIENT_CALENDAR_NAME = "clientCalendarName";
    public final static String JOB_TYPE = "jobType";
    public final static String PAYMENT_AMOUNT = "amount";
    public final static String JOB_TIME = "dayOfMonth";
    public final static String COMMENT = "comment";
    public final static String IS_PAID = "isPaid";
    public final static String INSERT_TIME= "insertTime";
    public final static String UPDATE_TIME = "updateTime";
    private final DBHelper dbHelper;
    private SQLiteDatabase db;


    public JobsDBHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    public long insert(Session newJob) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(JOB, newJob.job);
        values.put(CLIENT_NAME, newJob.clientName);
//        values.put(JOB_TYPE, newJob.jobType);
        values.put(PAYMENT_AMOUNT, newJob.amount);
        values.put(JOB_TIME, newJob.dayOfMonth);
        values.put(COMMENT, newJob.comment);
        values.put(IS_PAID, newJob.isPaid);
//        values.put(INSERT_TIME, newJob.insertTime);
        long insertedID = db.insert(TABLE_JOBS, null, values);

        db.close();

        return insertedID;
    }

    public Cursor getAllJobsCursor() {
        db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_JOBS;
        return db.rawQuery(sql, null);
    }

}
