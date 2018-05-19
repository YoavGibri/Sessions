package com.yoavgibri.myincome;

import android.app.Application;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.yoavgibri.myincome.FireBase.FBHelper;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Yoav on 11/03/17.
 */

public class App extends Application {

    public static FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MainActivity.IS_CRASHLYTICS_ON, false)) {
                startCrashLytics();
            }
        } else {
            startCrashLytics();
        }
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
    }

    public void startCrashLytics() {
        Fabric.with(this, new Crashlytics());
        Toast.makeText(this, "CrashLytics Started!", Toast.LENGTH_SHORT).show();
    }
}
