package com.yoavgibri.myincome.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yoavgibri.myincome.Models.CalEvent;
import com.yoavgibri.myincome.R;
import com.yoavgibri.myincome.Utils;
import com.yoavgibri.myincome.addUpdateSessionActivity;

import static com.yoavgibri.myincome.MainActivity.SESSION_ACTIVITY_STATE;
import static com.yoavgibri.myincome.Utils.logToFile;

/**
 * Created by Yoav on 22/07/17.
 */

public class PostSessionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            logToFile("PostSessionReceiver- Received");
            if (intent != null) {
                String eventJson = intent.getStringExtra(CalEvent.EVENT_JSON);
                CalEvent event = new Gson().fromJson(eventJson, CalEvent.class);
                logToFile("Event is " + event.title);
                PendingIntent newSessionPendingIntent = getPendingIntent(context, event);
                startNotification(context, newSessionPendingIntent, event);
            }
        } catch (Exception e) {
            logToFile(e.getMessage());
        }

    }


    private PendingIntent getPendingIntent(Context context, CalEvent event) {
        try {
            Intent resultIntent = new Intent(context, addUpdateSessionActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            String actionString = event.title;
            resultIntent.setAction(actionString);
            resultIntent.putExtra(SESSION_ACTIVITY_STATE, "New Session");
            return PendingIntent.getActivity(context, (int) (Math.random() * 100), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            logToFile(e.getMessage());
            return null;
        }
    }

    private void startNotification(Context context, PendingIntent resultPendingIntent, CalEvent event) {
        try {
            String title = event.title;
            String client = title;
//        int endOfClientIndex = title.length() - 1;
            int endOfClientIndex;
            if (title.contains(",")) {
                endOfClientIndex = title.indexOf(",");
                client = title.substring(0, endOfClientIndex);
            }
            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.unicorn_icon)
                    .setContentTitle("טיפול חדש")
                    .setContentText("האם היה טיפול ל" + client + "?")
                    //                .setContentText("Cal: " + event.calID + "\nEventId: " + event.eventId + "\nTitle: " + event.title
                    //                        + "\nStart: " + event.startDate + "\nEnd: " + event.endDate)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .build();
            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        } catch (Exception e) {
            logToFile(e.getMessage());
        }
    }

}
