package com.example.trafimau_app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAppInfo {
    public final String packageName;
    public final Drawable icon;
    public final Intent launchIntent;
    public final String label;
    public int launchedCount;
    public Date lastLaunched;

    public MyAppInfo(ApplicationInfo applicationInfo, PackageManager packageManager) {
        packageName = applicationInfo.packageName;
        label = packageManager.getApplicationLabel(applicationInfo).toString();
        launchIntent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
        icon = packageManager.getApplicationIcon(applicationInfo);
        launchedCount = 0;
    }

    public static Map<String, MyAppInfo> getPackageNameMapFromList(List<MyAppInfo> myAppInfoList) {
        Map<String, MyAppInfo> map = new HashMap<>();
        for (MyAppInfo app : myAppInfoList) {
            map.put(app.packageName, app);
        }
        return map;
    }
}
