package com.example.trafimau_app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class MyAppInfo {
    public ApplicationInfo applicationInfo;
    public Drawable icon;
    public Intent launchIntent;
    public String label;

    public MyAppInfo(ApplicationInfo applicationInfo, PackageManager packageManager) {
        this.applicationInfo = applicationInfo;
        this.icon = packageManager.getApplicationIcon(applicationInfo);
        this.launchIntent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
        this.label = packageManager.getApplicationLabel(applicationInfo).toString();
    }
}
