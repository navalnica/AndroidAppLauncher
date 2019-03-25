package com.example.trafimau_app.data;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.data.db.DateConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAppInfo {
    public final String packageName;
    public final Drawable icon;
    public final Intent launchIntent;
    public final String label;
    public boolean isSystemApp;
    public int launchedCount;
    public Date lastLaunched;
    public Date firstInstallTime;

    // todo: consider extending AppEntity
    public MyAppInfo(ApplicationInfo applicationInfo, PackageManager packageManager) {
        packageName = applicationInfo.packageName;
        label = packageManager.getApplicationLabel(applicationInfo).toString();
        launchIntent = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
        icon = packageManager.getApplicationIcon(applicationInfo);
        launchedCount = 0;
        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            isSystemApp = true;
        }
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            firstInstallTime = DateConverter.toDate(info.firstInstallTime);
            if(firstInstallTime == null){
                throw new NullPointerException("firstInstallTime is null for package " + packageName);
            }
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(MyApplication.LOG_TAG, "MyAppInfo. package not found");
        }
    }

    public static Map<String, MyAppInfo> getPackageNameMapFromList(List<MyAppInfo> myAppInfoList) {
        Map<String, MyAppInfo> map = new HashMap<>();
        for (MyAppInfo app : myAppInfoList) {
            map.put(app.packageName, app);
        }
        return map;
    }
}
