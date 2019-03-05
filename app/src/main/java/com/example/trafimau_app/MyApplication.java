package com.example.trafimau_app;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.trafimau_app.db.AppDatabase;
import com.example.trafimau_app.db.AppEntity;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    // TODO: update APP_CENTER_KEY
    private static final String APP_CENTER_KEY = "837acbd8-490f-4613-8c34-8cadf9bd3268";
    private final static String APP_METRICA_API_KEY = "2ade6b48-042d-4406-acbf-d59322bafa72";

    public final static String LOG_TAG = "MyApp";

    public DesktopSiteLinksDataModel sitesDataModel = new DesktopSiteLinksDataModel();

    private SharedPreferences sharedPreferences;

    private String showWelcomePageKey;
    private boolean showWelcomePage = false;

    private String nightModeEnabledKey;
    private boolean nightModeEnabled = false;

    private String compactLayoutEnabledKey;
    private boolean compactLayoutEnabled = false;

    private AppDatabase db;
    private ArrayList<MyAppInfo> installedApps = new ArrayList<>();
    private PackageManager pm;

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
        initExternalTrackingServicies();

        pm = getPackageManager();
        initDatabase();
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database-name")
                .allowMainThreadQueries() // TODO: run operations in background
                .build();

        // TODO: comment
//        db.clearAllTables();

        scanInstalledApps();
        updateAppsDB();
    }

    private void scanInstalledApps() {

        // TODO: it's called every app launch. make DB receive changes to PackageManager

        YandexMetrica.reportEvent("MyApplication.scanInstalledApps()");

        installedApps.clear();
        List<ApplicationInfo> infos = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ai : infos) {
            Intent intent = pm.getLaunchIntentForPackage(ai.packageName);
            if (intent != null) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    this.installedApps.add(new MyAppInfo(ai, pm));
                }
            }
        }
    }

    private void updateAppsDB() {
        // TODO: calculate diff with DiffUtils ?

        // TODO: when app is deleted and Launcher is not launched
        // DB automatically removes corresponding AppEntity
        // figure out why it's done
        List<AppEntity> dbAppsList = db.appEntityDao().getAll();
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "appsDB content:");
        for (AppEntity e : dbAppsList) {
            Log.d(LOG_TAG, e.toString());
        }

        Map<String, AppEntity> dbAppsMap = AppEntity.getMapFromList(dbAppsList);
        Map<String, MyAppInfo> scannedAppsMap = MyAppInfo.getMapFromList(installedApps);

        Set<String> newPackages = new HashSet<>(scannedAppsMap.keySet());
        newPackages.removeAll(dbAppsMap.keySet());
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "MyApplication.updateAppsDB: new packages cnt: " + newPackages.size());
        for (String packageName : newPackages) {
            final MyAppInfo scannedAppInfo = scannedAppsMap.get(packageName);
            final AppEntity entity = scannedAppInfo.getAppEntity();
            db.appEntityDao().insert(entity);
            Log.d(LOG_TAG, "MyApplication.updateAppsDB: adding package to DB: "
                    + entity.packageName);
        }

        Set<String> removedPackages = new HashSet<>(dbAppsMap.keySet());
        removedPackages.removeAll(scannedAppsMap.keySet());
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "MyApplication.updateAppsDB: removed cnt: " + removedPackages.size());
        for (String packageName : removedPackages) {
            final AppEntity appEntity = dbAppsMap.get(packageName);
            db.appEntityDao().delete(appEntity);
            Log.d(LOG_TAG, "MyApplication.updateAppsDB: removing package from DB: "
                    + appEntity.packageName);
        }

        Set<String> intersection = new HashSet<>(dbAppsMap.keySet());
        intersection.retainAll(scannedAppsMap.keySet());
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "MyApplication.updateAppsDB: intersection cnt: " + intersection.size());
        for (String packageName : intersection) {
            scannedAppsMap.get(packageName).launchedCount =
                    dbAppsMap.get(packageName).launchedCount;
        }

        // TODO: remove
        dbAppsList = db.appEntityDao().getAll();
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "updated appsDB content:");
        for (AppEntity e : dbAppsList) {
            Log.d(LOG_TAG, e.toString());
        }
        Log.d(LOG_TAG, "total cnt: " + dbAppsList.size());
        Log.d(LOG_TAG, " ");
    }

    public void savePackageToDB(@NonNull String packageName) {
        try {
            final ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            installedApps.add(new MyAppInfo(ai, pm));
            db.appEntityDao().insert(new AppEntity(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            String msg = "MyApplication.savePackageToDB: packageName not found exception";
            Log.e(LOG_TAG, msg);
        }
    }

    public void deletePackageFromDB(@NonNull String packageName) {
        db.appEntityDao().delete(new AppEntity(packageName, 0));
        for(int i = 0; i < installedApps.size(); ++i){
            if(installedApps.get(i).packageName.equals(packageName)){
                installedApps.remove(i);
                return;
            }
        }
    }

    public void increaseAppLaunchedCount(@NonNull String packageName, int installedAppsPosition) {
        AppEntity oldEntity = db.appEntityDao().getAppByPackageName(packageName);
        db.appEntityDao().insert(new AppEntity(packageName, oldEntity.launchedCount + 1));
        if (installedAppsPosition < 0 || installedAppsPosition >= installedApps.size()) {
            final String msg = "MyApplication.increaseAppLaunchedCount: invalid installedAppsPosition passed";
            Log.e(LOG_TAG, msg);
            throw new InvalidParameterException(msg);
        }
        installedApps.get(installedAppsPosition).launchedCount++;
    }

    public int getInstalledAppsCount() {
        return installedApps.size();
    }

    public MyAppInfo getInstalledAppInfo(int i) {
        if (i < 0 || i >= installedApps.size()) {
            final String msg = "MyApplication.getInstalledAppInfo: invalid array position passed";
            Log.e(LOG_TAG, msg);
            throw new InvalidParameterException(msg);
        }
        return installedApps.get(i);
    }

    private void initExternalTrackingServicies() {
        Fabric.with(this, new Crashlytics());

        AppCenter.start(this, APP_CENTER_KEY, Distribute.class);

        // Create extended configuration library for AppMetrica
        YandexMetricaConfig config = YandexMetricaConfig
                .newConfigBuilder(APP_METRICA_API_KEY).build();
        // initialize AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // app lifecycle tracking
        YandexMetrica.enableActivityAutoTracking(this);
    }

    private void getDataFromSharedPreferences() {
        showWelcomePageKey = getString(R.string.showWelcomePageKey);
        showWelcomePage = sharedPreferences.getBoolean(showWelcomePageKey, true);

        nightModeEnabledKey = getString(R.string.sharedPrefNightModeEnabledKey);
        nightModeEnabled = sharedPreferences.getBoolean(nightModeEnabledKey, false);

        compactLayoutEnabledKey = getString(R.string.sharedPrefCompactLayoutEnabledKey);
        compactLayoutEnabled = sharedPreferences.getBoolean(compactLayoutEnabledKey, false);
        currentLayout = compactLayoutEnabled ? LayoutInfo.COMPACT : LayoutInfo.STANDARD;
    }

    private void syncAppTheme() {
        if (nightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public boolean isShowWelcomePage() {
        return showWelcomePage;
    }

    public void setShowWelcomePage(boolean value) {
        showWelcomePage = value;
        sharedPreferences
                .edit()
                .putBoolean(showWelcomePageKey, showWelcomePage)
                .apply();

        Log.d(LOG_TAG, "showWelcomePage: " + value);
        YandexMetrica.reportEvent("setting show welcome page on next run to " + value);
    }

    public boolean isNighModeEnabled() {
        return nightModeEnabled;
    }

    public void setNightModeEnabled(boolean value) {
        nightModeEnabled = value;
        syncAppTheme();

        sharedPreferences
                .edit()
                .putBoolean(nightModeEnabledKey, value)
                .apply();

        String msg = "Dark theme " + (value ? "enabled" : "disabled");
        Log.d(LOG_TAG, msg);
        YandexMetrica.reportEvent(msg);
    }

    public boolean isCompactLayoutEnabled() {
        return compactLayoutEnabled;
    }

    public void setCompactLayoutEnabled(boolean value) {
        compactLayoutEnabled = value;
        currentLayout = compactLayoutEnabled ? LayoutInfo.COMPACT : LayoutInfo.STANDARD;
        sharedPreferences
                .edit()
                .putBoolean(compactLayoutEnabledKey, compactLayoutEnabled)
                .apply();

        final String msg = "compactLayoutEnabled: " + compactLayoutEnabled;
        Log.d(MyApplication.LOG_TAG, msg);
        YandexMetrica.reportEvent(msg);
    }
}
