package com.yoavgibri.myincome.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yoavgibri.myincome.Models.Client;

/**
 * Created by Yoav on 03/08/16.
 */
public class ClientsDBHelper {



    public final static String TABLE_CLIENTS = "clients";
    public final static String CLIENT_ID = "client";
    public final static String CLIENT_NAME = "clientName";
    private final DBHelper dbHelper;
    private SQLiteDatabase db;


    public ClientsDBHelper(Context context, DBHelper dbHelper) {
//        super(context, DBHelper.DATABASE_NAME, null, DBHelper.DB_VERSION);
        this.dbHelper = dbHelper;
    }



    public void insertNewClientName(Client newClient) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLIENT_NAME, newClient.clientName);
        db.insert(TABLE_CLIENTS, null, values);
        db.close();
    }

    public Cursor getClientsCursor() {
        db = dbHelper.getReadableDatabase();
        Cursor c = db.query(TABLE_CLIENTS, null, null, null, null, null, null);
        DatabaseUtils.dumpCursor(c);
        return c;
//        return null;
    }

    public Cursor getClientsFilteredCursor(CharSequence filter) {
        String select = "(" + CLIENT_NAME + " LIKE ? ";
        String[]  selectArgs = { "%" + filter + "%"};
        Cursor c = db.query(TABLE_CLIENTS, null, select, selectArgs, null, null, null);
        DatabaseUtils.dumpCursor(c);
        return c;
    }





//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }

}
