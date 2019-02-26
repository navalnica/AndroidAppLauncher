package com.example.trafimau_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataModel {

    public ArrayList<MyAppInfo> apps;

    public void syncWithInstalledAppsInfo(Context context) {

        final PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);


        apps = new ArrayList<>();

        for (ApplicationInfo ai : installedApps) {
            Intent intent = pm.getLaunchIntentForPackage(ai.packageName);
            if (intent != null) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    apps.add(new MyAppInfo(ai, pm));
                }
            }
        }
    }

    // TODO: remove methods
    private void printLabels(List<String> labels) {
        Log.d(MyApplication.LOG_TAG, "labels:");
        for (String l : labels) {
            Log.d(MyApplication.LOG_TAG, l);
        }
        Log.d(MyApplication.LOG_TAG, "total count: " + labels.size());
        Log.d(MyApplication.LOG_TAG, "total count: " + labels.size());
        Log.d(MyApplication.LOG_TAG, " ");
    }

}
