package com.example.trafimau_app.data.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(tableName = DesktopSiteItem.TABLE_NAME)
@TypeConverters(UriConverter.class)
public class DesktopSiteItem {

    public static final String TABLE_NAME = "desktop_site_items";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_INDEX = "_index";
    public static final String COLUMN_SHORT_LINK = "short_link";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_PATH_TO_ICON_CACHE = "path_to_icon_cache";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    public long id;

    @ColumnInfo(name = COLUMN_INDEX)
    public int index;

    @ColumnInfo(name = COLUMN_SHORT_LINK)
    public String shortLink;

    @ColumnInfo(name = COLUMN_URI)
    public Uri uri;

    @ColumnInfo(name = COLUMN_PATH_TO_ICON_CACHE)
    public String pathToIconCache;

    public DesktopSiteItem(long id, int index, String shortLink, Uri uri, String pathToIconCache) {
        this.index = index;
        this.shortLink = shortLink;
        this.uri = uri;
        this.pathToIconCache = pathToIconCache;
    }

    public DesktopSiteItem(int index, @NonNull String siteLink) {
        this.index = index;

        String pattern = "^(https?://)?([\\w.]+)/?(.*)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(siteLink);
        if (m.find()) {
            this.shortLink = m.group(2);
            if (m.group(1) == null || m.group(1).isEmpty()) {
                siteLink = "https://" + siteLink;
            }
            this.uri = Uri.parse(siteLink);
        } else {
            throw new IllegalArgumentException("could not parse siteLink");
        }
    }

}

