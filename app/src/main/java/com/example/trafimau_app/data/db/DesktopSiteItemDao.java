package com.example.trafimau_app.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DesktopSiteItemDao {

    @Query("SELECT * FROM " + DesktopSiteItem.TABLE_NAME)
    List<DesktopSiteItem> getAllInList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DesktopSiteItem desktopSiteItem);

    @Update
    void update(DesktopSiteItem desktopSiteItem);

    @Query("UPDATE " + DesktopSiteItem.TABLE_NAME +
            " SET " + DesktopSiteItem.COLUMN_PATH_TO_ICON_CACHE + " = :absolutePath WHERE "+
            DesktopSiteItem.COLUMN_INDEX + " = :index")
    void updatePathToIconCacheFile(int index, String absolutePath);

    @Delete
    void delete(DesktopSiteItem desktopSiteItem);

}
