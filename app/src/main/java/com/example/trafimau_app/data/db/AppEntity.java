package com.example.trafimau_app.data.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.trafimau_app.data.MyAppInfo;
import com.example.trafimau_app.MyApplication;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = AppEntity.TABLE_NAME)
@TypeConverters(DateConverter.class)
public class AppEntity {

    public static final String TABLE_NAME = "apps";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_PACKAGE_NAME = "package_name";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_IS_SYSTEM_APP = "is_system_app";
    public static final String COLUMN_LAUNCHED_COUNT = "launched_count";
    public static final String COLUMN_LAST_LAUNCHED = "last_launched";
    public static final String COLUMN_FIRST_INSTALL_TIME = "first_install_time";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @NonNull
    @ColumnInfo(name = COLUMN_PACKAGE_NAME)
    public String packageName;

    @NonNull
    @ColumnInfo(name = COLUMN_LABEL)
    public String label;

    @ColumnInfo(name = COLUMN_IS_SYSTEM_APP)
    public boolean isSystemApp;

    @ColumnInfo(name = COLUMN_LAUNCHED_COUNT)
    public int launchedCount;

    @ColumnInfo(name = COLUMN_LAST_LAUNCHED)
    public Date lastLaunched;

    @ColumnInfo(name = COLUMN_FIRST_INSTALL_TIME)
    public Date firstInstallTime;

    public AppEntity(@NonNull String packageName, @NonNull String label,
                     boolean isSystemApp, @NonNull Date firstInstallTime) {
        this.packageName = packageName;
        this.label = label;
        this.isSystemApp = isSystemApp;
        this.firstInstallTime = firstInstallTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "package: " + packageName + "; label: " + label +
                "; launched: " + launchedCount + "; isSystemApp: " + isSystemApp;
    }

    @NonNull
    public static AppEntity fromMyAppInfo(@NonNull MyAppInfo appInfo) {
        if (appInfo.packageName == null || appInfo.label == null) {
            final String msg = "AppEntity.fromMyAppInfo: appInfo has null fields";
            Log.e(MyApplication.LOG_TAG, msg);
            throw new NullPointerException(msg);
        }
        AppEntity e = new AppEntity(appInfo.packageName, appInfo.label,
                appInfo.isSystemApp, appInfo.firstInstallTime);
        if (appInfo.launchedCount > 0) {
            e.launchedCount = appInfo.launchedCount;
            if (appInfo.lastLaunched == null) {
                final String msg = "AppEntity.fromMyAppInfo: appInfo.lastLaunched is null when it shouldn't be null";
                throw new NullPointerException(msg);
            }
            e.lastLaunched = appInfo.lastLaunched;
        }
        return e;
    }

    public static Map<String, AppEntity> getPackageNameMapFromList(List<AppEntity> entities) {
        Map<String, AppEntity> map = new HashMap<>();
        for (AppEntity e : entities) {
            map.put(e.packageName, e);
        }
        return map;
    }
}
