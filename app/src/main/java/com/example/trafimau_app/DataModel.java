package com.example.trafimau_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DataModel {

    // TODO: change item count to 1_000
    private final int itemCount = 200;
    private final ArrayList<Integer> colors = new ArrayList<>();
    public ArrayList<MyAppInfo> apps;

    public DataModel() {
        for (int i = 0; i < itemCount; ++i) {
            int color = generateColor();
            colors.add(color);
        }
    }

    private int generateColor() {
        Random random = new Random();
        return Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    public int getItemCount() {
        return itemCount;
    }

    public Integer getColor(int position) {
        if (position >= 0 && position < itemCount) {
            return colors.get(position);
        }
        return null;
    }

    public void syncWithInstalledAppsInfo(Context context) {

        final PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);


        apps = new ArrayList<>();

//        List<String> labels = new ArrayList<>();

        for (ApplicationInfo ai : installedApps) {
            Intent intent = pm.getLaunchIntentForPackage(ai.packageName);
            if (intent != null) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {

//                    final String label = pm.getApplicationLabel(ai).toString();
//                    labels.add(label);
                    apps.add(new MyAppInfo(ai, pm));
                }
            }
        }

//        Collections.sort(labels);
//        printLabels(labels);
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

    private void logAppInfo(ApplicationInfo ai, PackageManager pm) {
        Log.d(MyApplication.LOG_TAG, "label: " + pm.getApplicationLabel(ai));
        Log.d(MyApplication.LOG_TAG, "Source dir: " + ai.sourceDir);
        Intent intent = pm.getLaunchIntentForPackage(ai.packageName);
        if (intent != null) {
            Log.d(MyApplication.LOG_TAG, "action: " + intent.getAction());
            Log.d(MyApplication.LOG_TAG, "categories: " + intent.getCategories());
        }
        Log.d(MyApplication.LOG_TAG, " ");
    }

}
