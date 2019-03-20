package com.example.trafimau_app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.trafimau_app.data.db.AppDao;
import com.example.trafimau_app.data.db.AppsDatabase;

public class AppsLaunchCountProvider extends ContentProvider {

    // TODO: can I use authority different from one defined under <provider> in manifest?
    public static final String AUTHORITY = "com.example.trafimau_app.provider";

    private static final int CODE_LAUNCHED_ALL = 1;
    private static final int CODE_LAUNCHED_LAST = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // TODO: is "content://" schema added in the beginning ?
        uriMatcher.addURI(AUTHORITY, AppsDatabase.DB_NAME, CODE_LAUNCHED_ALL);
        uriMatcher.addURI(AUTHORITY, AppsDatabase.DB_NAME + "/last", CODE_LAUNCHED_LAST);
    }

    private AppDao appDao;

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        if (context == null) {
            final String msg = "AppsLaunchCountProvider.onCreate(): context = null";
            throw new NullPointerException(msg);
        }
        appDao = AppsDatabase.getInstance(context).appEntityDao();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        final Cursor cursor;
        switch(uriMatcher.match(uri)){
            case(CODE_LAUNCHED_ALL):
                cursor = appDao.getLaunchedInCursor();
                break;
            case(CODE_LAUNCHED_LAST):
                cursor = appDao.getLastLaunchedInCursor();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if(cursor == null){
            final String msg = "AppsLaunchCountProvider.query(): cursor is null";
            throw new NullPointerException(msg);
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        return 0;
    }
}
