package com.example.trafimau_app.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.MyApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = AppEntity.TABLE_NAME)
public class AppEntity {

    public static final String TABLE_NAME = "apps";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PACKAGE_NAME = "package_name";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_LAUNCHED_COUNT = "launched_count";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @NonNull
    @ColumnInfo(name = COLUMN_PACKAGE_NAME)
    public String packageName;

    @NonNull
    @ColumnInfo(name = COLUMN_LABEL)
    public String label;

    @ColumnInfo(name = COLUMN_LAUNCHED_COUNT)
    public int launchedCount;

    public AppEntity(@NonNull String packageName, @NonNull String label, int launchedCount) {
        this.packageName = packageName;
        this.label = label;
        this.launchedCount = launchedCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "package: " + packageName + "; label: " + label + "; launched: " + launchedCount;
    }

    @NonNull
    public static AppEntity fromMyAppInfo(@NonNull MyAppInfo appInfo) {
        if (appInfo.packageName == null || appInfo.label == null) {
            final String msg = "AppEntity.fromMyAppInfo: appInfo has null fields";
            Log.e(MyApplication.LOG_TAG, msg);
            throw new NullPointerException(msg);
        }
        return new AppEntity(appInfo.packageName, appInfo.label, appInfo.launchedCount);
    }

    public static Map<String, AppEntity> getPackageNameMapFromList(List<AppEntity> entities) {
        Map<String, AppEntity> map = new HashMap<>();
        for (AppEntity e : entities) {
            map.put(e.packageName, e);
        }
        return map;
    }
}
