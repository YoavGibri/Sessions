package com.yoavgibri.myincome.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Yoav on 30/07/17.
 */

public class PreSessionReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("0544980530", null, "שלום עולם", null, null);
    }
}
