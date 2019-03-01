package com.example.trafimau_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.List;

public class AppsDataModel {

    public final ArrayList<MyAppInfo> apps = new ArrayList<>();

    public void getInstalledAppsInfo(Context context) {

        YandexMetrica.reportEvent("AppsDataModel: getting installed apps");

        apps.clear();

        final PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ai : installedApps) {
            Intent intent = pm.getLaunchIntentForPackage(ai.packageName);
            if (intent != null) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    apps.add(new MyAppInfo(ai, pm));
                }
            }
        }
    }

    // TODO: remove method. it's needed to check total number of detected apps on device
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
