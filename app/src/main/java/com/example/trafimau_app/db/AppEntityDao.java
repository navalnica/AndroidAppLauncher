package com.example.trafimau_app.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

@Dao
public interface AppEntityDao {

    @Query("SELECT * FROM AppEntity")
    List<AppEntity> getAll();

    @Query("SELECT * FROM AppEntity where packageName like :packageName")
    AppEntity getAppByPackageName(@NonNull String packageName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppEntity appEntity);

    @Delete
    void delete(AppEntity appEntity);
}
