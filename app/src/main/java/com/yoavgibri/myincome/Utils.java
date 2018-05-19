package com.yoavgibri.myincome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Yoav on 25/02/17.
 */

public class Utils {


    public static boolean isHebrewLocale(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().equals(new Locale("he").getLanguage());
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void logToFile(String text) {
        File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myIncomeLog.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            String methodName = "MethodName UnKnown";
            if (ste[3] != null) {
                methodName = ste[3].getMethodName();
            }
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf
                    .append(sdf.format(Calendar.getInstance().getTime()))
                    .append("  ---  ")
                    .append(methodName)
                    .append(" - ")
                    .append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convert(Context context, int textRes) {
        return convert(context, context.getString(textRes));
    }

    public static String convert(Context context, String text) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sessionName = sp.getString(context.getString(R.string.key_session_name_single), "");
        String sessionsName = sp.getString(context.getString(R.string.key_sessions_name_plural), "");

        if (!sessionsName.isEmpty()) {
            text = text.replace("sessions", sessionsName);
            text = text.replace("Sessions", sessionsName.substring(0, 1).toUpperCase() + sessionsName.substring(1));
        }

        if (!sessionName.isEmpty()) {
            text = text.replace("session", sessionName);
            text = text.replace("Session", sessionName.substring(0, 1).toUpperCase() + sessionName.substring(1));
        }

        return text;
    }
}
