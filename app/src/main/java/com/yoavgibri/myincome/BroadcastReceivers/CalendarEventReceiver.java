package com.yoavgibri.myincome.BroadcastReceivers;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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

import com.google.gson.Gson;
import com.yoavgibri.myincome.Models.CalEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.yoavgibri.myincome.R.string.key_work_calendar;
import static com.yoavgibri.myincome.Utils.logToFile;

/**
 * Created by Yoav on 23/07/16.
 */
public class CalendarEventReceiver extends BroadcastReceiver {

    //region projection
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.CALENDAR_ID,    // 0
            CalendarContract.Events._ID,            // 1
            CalendarContract.Events.TITLE,          // 2
            CalendarContract.Events.DTSTART,        // 3
            CalendarContract.Events.DTEND           // 4
    };

    // The indices for the projection array above.
    private static final int PROJECTION_CALENDAR_ID_INDEX = 0;
    private static final int PROJECTION_EVENT_ID_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;
    private static final int PROJECTION_DATE_START_INDEX = 3;
    private static final int PROJECTION_DATE_END_INDEX = 4;

    //endregion

    private SharedPreferences SP;
    String TAG = "com.yoavgibri.myincome";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            logToFile("CalendarEventReceiver- Received, " + intent.getData().toString());
            if (intent.getData().toString().equalsIgnoreCase("content://com.android.calendar")) {

                //query CHOSEN calendar and compare to SP last event, to check if it is a new SESSION event:
                CalEvent event = queryLastSessionsCalEvent(context);
                if (event == null) {
                    logToFile("Even is null");
                    return;
                }
                logToFile("Even is " + event.title);

                //set an alarm manager for 2 (7200000) hours after that event's start time.
                setNotificationAlarm(context, event);


            }
        } catch (Exception e) {
            logToFile(e.getMessage());
        }
    }


    private CalEvent queryLastSessionsCalEvent(Context context) {
        try {
            SP = PreferenceManager.getDefaultSharedPreferences(context);
            Cursor cur = null;
            ContentResolver cr = context.getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";
            String[] selectionArgs = new String[]{SP.getString(context.getString(key_work_calendar), "999")};

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
//        DatabaseUtils.dumpCursor(cur);

            if (cur != null && cur.moveToLast()) {
                CalEvent lastEvent = new CalEvent();

                // Get the field values
                lastEvent.calID = cur.getLong(PROJECTION_CALENDAR_ID_INDEX);
                lastEvent.eventId = cur.getLong(PROJECTION_EVENT_ID_INDEX);
                lastEvent.title = cur.getString(PROJECTION_TITLE_INDEX);
                lastEvent.startDate = cur.getString(PROJECTION_DATE_START_INDEX);
                lastEvent.endDate = cur.getString(PROJECTION_DATE_END_INDEX);
                cur.close();

                if (lastEvent.eventId != SP.getLong("last_event_Id", 0)) {
                    SP.edit().putLong("last_event_Id", lastEvent.eventId).apply();
                    return lastEvent;
                }
            }
        } catch (Exception e) {
            logToFile(e.getMessage());
        }
        return null;
    }


    private void setNotificationAlarm(Context context, CalEvent event) {
        try {
            //the alarm triggers a notification asking about the job, with pending intent to insert a new Session activity.
            Intent intentPostSession = new Intent(context, PostSessionReceiver.class);
            String eventJson = new Gson().toJson(event, CalEvent.class);
            intentPostSession.putExtra(CalEvent.EVENT_JSON, eventJson);

            long alarmDelay = PreferenceManager.getDefaultSharedPreferences(context).getLong("alarm_delay", 7200000);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, (int) event.eventId, intentPostSession, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        Log.d(TAG, "Current time: " + Calendar.getInstance().getTimeInMillis());
//        Log.d(TAG, "event Time: " + Long.parseLong(event.startDate));
//        Log.d(TAG, "Delay: " + alarmDelay);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Long.parseLong(event.startDate) + alarmDelay, pendingIntent);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pendingIntent);

            // LOG
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(event.startDate) + alarmDelay);
            String notificationTime = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(cal.getTime());
            logToFile("Notification set to " + notificationTime);
        } catch (Exception e) {
            logToFile(e.getMessage());
        }
    }


    private void setSmsAlarm(Context context, CalEvent event) {

        try {
            Intent intentPreSession = new Intent(context, PreSessionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, (int) event.eventId, intentPreSession, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        Log.d(TAG, "Current time: " + Calendar.getInstance().getTimeInMillis());
//        Log.d(TAG, "event Time: " + Long.parseLong(event.startDate));
//        Log.d(TAG, "Delay: " + alarmDelay);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            logToFile(e.getMessage());
        }
    }


}
