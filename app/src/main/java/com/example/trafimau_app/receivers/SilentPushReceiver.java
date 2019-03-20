package com.example.trafimau_app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.yandex.metrica.push.YandexMetricaPush;
public class SilentPushReceiver extends BroadcastReceiver {

    private final String karmaScheme = "karma";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract push message payload from your push message.
        String payload = intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD);
        if(payload == null){
            Log.d(MyApplication.LOG_TAG, "SilentPushReceiver.onReceive: stringExtra is null");
            return;
        }

        Log.d(MyApplication.LOG_TAG, "SilentPushReceiver.onReceive: payload: " + payload);
        Uri uri = Uri.parse(payload);

        final String scheme = uri.getScheme();
        if(scheme == null){
            Log.d(MyApplication.LOG_TAG, "SilentPushReceiver.onReceive: payload's scheme is null");
            return;
        }

        if(scheme.equals(karmaScheme)){
            String valString = uri.getAuthority();
            if(valString == null){
                Log.d(MyApplication.LOG_TAG, "SilentPushReceiver.onReceive: payload's authority is null");
                return;
            }

            try{
                int newKarmaValue = Integer.parseInt(valString);
                MyApplication app = (MyApplication) context.getApplicationContext();
                Log.d(MyApplication.LOG_TAG, "SilenPushReceiver: updating karma to " + newKarmaValue);
                app.setKarmaValue(newKarmaValue);
                app.setKarmaLastChangeDateToNow();
                app.sendBroadcast(new Intent(MyApplication.KARMA_UPDATED_FROM_SILENT_PUSH_ACTION));
            }
            catch (NumberFormatException e){
                Log.e(MyApplication.LOG_TAG, "Could not parse karma value from silent push payload");
            }
            catch (Exception e){
                Log.e(MyApplication.LOG_TAG, "Some other exception occured in SilentPushReceiver:");
                Log.e(MyApplication.LOG_TAG, e.getMessage());
            }
        }
    }
}