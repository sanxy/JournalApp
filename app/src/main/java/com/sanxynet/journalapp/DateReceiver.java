package com.sanxynet.journalapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
            // handle date change and mainActivity running
        }
    }
}
