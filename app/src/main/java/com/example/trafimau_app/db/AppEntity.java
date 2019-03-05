package com.example.trafimau_app.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class AppEntity {

    @PrimaryKey
    @NonNull
    public String packageName;

    public int launchedCount;

    public AppEntity(String packageName, int launchedCount) {
        this.packageName = packageName;
        this.launchedCount = launchedCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "package: " + packageName + "; launched: " + launchedCount;
    }

    public static Map<String, AppEntity> getMapFromList(List<AppEntity> entities) {
        Map<String, AppEntity> map = new HashMap<>();
        for (AppEntity e : entities) {
            map.put(e.packageName, e);
        }
        return map;
    }
}
