package com.yoavgibri.myincome.Managers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CalendarsManager {

    private static final String[] CALENDARS_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    private static final String TAG = "CalendarsManager";
    private final Context context;
    private final Cursor calendarsCursor;

    public CalendarsManager(Context context) {
        this.context = context;
        calendarsCursor = getDeviceCalendars();
    }


    private Cursor getDeviceCalendars() {
        // Run query
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "";
        String[] selectionArgs = new String[]{};
        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return cr.query(uri, CALENDARS_PROJECTION, selection, selectionArgs, null);
    }

    public CharSequence[] getCalendarsIds() {
        ArrayList<String> arrayListIds = new ArrayList<>();
        if (calendarsCursor != null) {
            while (calendarsCursor.moveToNext()) {
                int calendarId = calendarsCursor.getInt(calendarsCursor.getColumnIndex(CalendarContract.Calendars._ID));
                arrayListIds.add(String.valueOf(calendarId));
            }
            calendarsCursor.moveToPosition(-1);
        }
        CharSequence[] arrayIds = new CharSequence[arrayListIds.size()];
        arrayIds = arrayListIds.toArray(arrayIds);
        return arrayIds;
    }

    public CharSequence[] getCalendarsNames() {
        ArrayList<String> arrayListNames = new ArrayList<>();

        if (calendarsCursor != null) {
            while (calendarsCursor.moveToNext()) {
                String calendarName = calendarsCursor.getString(calendarsCursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
                arrayListNames.add(calendarName);
            }
            calendarsCursor.moveToPosition(-1);
        }
        CharSequence[] arrayNames = new CharSequence[arrayListNames.size()];
        arrayNames = arrayListNames.toArray(arrayNames);
        return arrayNames;
    }


}