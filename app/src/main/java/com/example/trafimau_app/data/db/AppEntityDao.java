package com.example.trafimau_app.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.List;

@Dao
public interface AppEntityDao {

    @Query("SELECT * FROM " + AppEntity.TABLE_NAME +
            " ORDER BY " + AppEntity.COLUMN_LAUNCHED_COUNT + " DESC, " +
            AppEntity.COLUMN_LABEL + " ASC")
    List<AppEntity> getAllInList();

    @Query("SELECT * FROM " + AppEntity.TABLE_NAME +
            " WHERE " + AppEntity.COLUMN_LAUNCHED_COUNT + " > 0" +
            " ORDER BY " + AppEntity.COLUMN_LAUNCHED_COUNT + " DESC, " +
            AppEntity.COLUMN_LABEL + " ASC")
    Cursor getLaunchedInCursor();

    @Query("SELECT * FROM " + AppEntity.TABLE_NAME +
            " WHERE " + AppEntity.COLUMN_PACKAGE_NAME + " = :packageName")
    AppEntity getAppByPackageName(@NonNull String packageName);

    @Query("SELECT * FROM " + AppEntity.TABLE_NAME +
            " WHERE " + AppEntity.COLUMN_LAST_LAUNCHED +
            " = (SELECT max(" + AppEntity.COLUMN_LAST_LAUNCHED +
            ") FROM " + AppEntity.TABLE_NAME + ")")
    Cursor getLastLaunchedInCursor();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppEntity appEntity);

    @Update
    void update(AppEntity appEntity);

    @Delete
    void delete(AppEntity appEntity);
}
