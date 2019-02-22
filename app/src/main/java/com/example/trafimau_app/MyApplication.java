package com.example.trafimau_app;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class MyApplication extends Application {

    public final static String LOG_TAG = "MyApp";

    public DataModel dataModel = new DataModel();

    private SharedPreferences sharedPreferences;

    private String nightModeEnabledKey;
    private boolean nightModeEnabled = false;
    
    private String compactLayoutEnabledKey;
    private boolean compactLayoutEnabled = false;
    
    public enum LayoutInfo {
        STANDARD(4, 6),
        COMPACT(5, 7);

        public final int portraitGridSpanCount;
        public final int landscapeGridSpanCount;

        LayoutInfo(int portraitGridSpanCount, int landscapeGridSpanCount) {
            this.portraitGridSpanCount = portraitGridSpanCount;
            this.landscapeGridSpanCount = landscapeGridSpanCount;
        }
    }

    public LayoutInfo currentLayout = LayoutInfo.STANDARD;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getDataFromSharedPreferences();
        syncAppTheme();
    }

    private void getDataFromSharedPreferences(){
        nightModeEnabledKey = getString(R.string.sharedPrefNightModeEnabledKey);
        nightModeEnabled = sharedPreferences.getBoolean(nightModeEnabledKey, false);

        compactLayoutEnabledKey = getString(R.string.sharedPrefCompactLayoutEnabledKey);
        compactLayoutEnabled = sharedPreferences.getBoolean(compactLayoutEnabledKey, false);
        currentLayout = compactLayoutEnabled ? LayoutInfo.COMPACT : LayoutInfo.STANDARD;
    }

    public void syncAppTheme(){
        if(nightModeEnabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    
    public boolean isNighModeEnabled(){
        return nightModeEnabled;
    }

    public void setNightModeEnabled(boolean value){
        nightModeEnabled = value;
        sharedPreferences
                .edit()
                .putBoolean(nightModeEnabledKey, value)
                .apply();
    }

    public boolean isCompactLayoutEnabled() {
        return compactLayoutEnabled;
    }

    public void setCompactLayoutEnabled(boolean value) {
        compactLayoutEnabled = value;
        Log.d(MyApplication.LOG_TAG, "MyApplication: compactLayoutEnabled: " + compactLayoutEnabled);
        currentLayout = value ? LayoutInfo.COMPACT : LayoutInfo.STANDARD;
        sharedPreferences
                .edit()
                .putBoolean(compactLayoutEnabledKey, value)
                .apply();
    }
}
