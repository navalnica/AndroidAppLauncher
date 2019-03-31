package com.example.trafimau_app.data.db;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class UriConverter {
    @TypeConverter
    public static Uri toUri (String uriString) {
        return uriString == null ? null : Uri.parse(uriString);
    }

    @TypeConverter
    public static String toString(Uri uri){
        return uri == null ? null : uri.toString();
    }
}