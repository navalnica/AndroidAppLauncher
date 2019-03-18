package com.example.trafimau_app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.yandex.metrica.push.YandexMetricaPush;
public class SilentPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract push message payload from your push message.
        String payload = intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD);
        Log.d(MyApplication.LOG_TAG, "received silent push. payload: " + payload);
    }
}