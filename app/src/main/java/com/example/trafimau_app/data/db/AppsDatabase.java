package com.example.trafimau_app.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {AppEntity.class, DesktopSiteItem.class}, version = 3)
public abstract class AppsDatabase extends RoomDatabase {

    public static final String DB_NAME = "database";

    public abstract AppDao appEntityDao();
    public abstract DesktopSiteItemDao desktopSiteItemDao();

    // The only instance
    private static AppsDatabase sInstance;

    public static synchronized AppsDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), AppsDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }
}