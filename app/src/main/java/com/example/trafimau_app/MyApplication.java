package com.example.trafimau_app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.trafimau_app.activity.launcher.ActivityLauncher;
import com.example.trafimau_app.data.DesktopSiteLinksDataModel;
import com.example.trafimau_app.data.MyAppInfo;
import com.example.trafimau_app.data.db.AppEntity;
import com.example.trafimau_app.data.db.AppsDatabase;
import com.example.trafimau_app.data.db.DateConverter;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.fabric.sdk.android.Fabric;

// todo: move work with SharedPrefs in separate helper class

public class MyApplication extends Application {

    private static final String APP_CENTER_KEY = "00de3ebf-a55b-4849-a9fe-3de0e018e1ca";
    private final static String APP_METRICA_API_KEY = "2ade6b48-042d-4406-acbf-d59322bafa72";

    public final static String LOG_TAG = "myapp";
    public final static String KARMA_UPDATED_FROM_SILENT_PUSH_ACTION =
            "com.example.trafimau_app.karma_updated_from_silent_push_action";

    public DesktopSiteLinksDataModel sitesDataModel = new DesktopSiteLinksDataModel();

    private ActivityLauncher activityLauncher;
    private SharedPreferences sharedPreferences;
    private AppsDatabase db;
    private PackageManager pm;
    private ArrayList<MyAppInfo> installedApps = new ArrayList<>();

    private String myPackageName;
    private boolean isDefaultSystemLauncher;

    private String showWelcomePageKey;
    private boolean showWelcomePage = false;

    private String nightModeEnabledKey;
    private boolean nightModeEnabled = false;

    private String compactLayoutEnabledKey;
    private boolean compactLayoutEnabled = false;

    private String karmaKey;
    private int karmaValue;

    private String karmaLastChangeDateKey;
    private Long karmaLastChangeDateValue;

    private String isSortAscendingKey;
    private boolean isSortAscending;

    private String sortModeKey;
    private String sortMode;
    private String SORT_MODE_NO_SORT;
    private String SORT_MODE_NAME;
    private String SORT_MODE_FREQUENCY;
    private String SORT_MODE_INSTALLATION_DATE;

    private String languageKey;
    private String language;
    private String LANGUAGE_ENGLISH;
    private String LANGUAGE_BELARUSIAN;
    private String LANGUAGE_RUSSIAN;
    private String LANGUAGE_SYSTEM_DEFAULT;

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
        initExternalTrackingServices();

        pm = getPackageManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myPackageName = getPackageName();
        initIsDefaultSystemLauncher();

        getDataFromSharedPreferences();
        syncAppTheme();
        initDatabase();
        registerNotificationChannel();
    }

    public void setActivityLauncher(ActivityLauncher activityLauncher) {
        this.activityLauncher = activityLauncher;
    }

    private void initDatabase() {
        db = AppsDatabase.getInstance(getApplicationContext());

        // uncomment to clear tables
//        db.clearAllTables();

        scanInstalledApps();
        updateAppsDB();
        sortInstalledApps();
    }

    private void sortInstalledApps() {
        if (SORT_MODE_FREQUENCY.equals(sortMode)) {
            Collections.sort(installedApps, (o1, o2) -> {
                int res = o2.launchedCount - o1.launchedCount;
                if (res != 0) {
                    return res;
                }
                if (isSortAscending) {
                    return o2.label.compareTo(o1.label);
                }
                return o1.label.compareTo(o2.label);
            });

        } else if (SORT_MODE_INSTALLATION_DATE.equals(sortMode)) {
            Collections.sort(installedApps,
                    (o1, o2) -> o2.firstInstallTime.compareTo(o1.firstInstallTime));
        } else if (SORT_MODE_NAME.equals(sortMode)) {
            Collections.sort(installedApps, (o1, o2) -> o2.label.compareTo(o1.label));
        }
        if (isSortAscending) {
            Collections.reverse(installedApps);
        }
        if (activityLauncher != null) {
            activityLauncher.notifyAppsChanged();
        }
    }

    private void scanInstalledApps() {
        installedApps.clear();
        List<ApplicationInfo> infos = pm
                .getInstalledApplications(0);

        for (ApplicationInfo ai : infos) {
            if (ai.packageName.equals(myPackageName)) {
                continue;
            }
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
        // figure out why it happens

        List<AppEntity> dbAppsList = db.appEntityDao().getAllInList();
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "appsDB content:");
        for (AppEntity e : dbAppsList) {
            Log.d(LOG_TAG, e.toString());
        }

        Map<String, AppEntity> dbAppsMap = AppEntity.getPackageNameMapFromList(dbAppsList);
        Map<String, MyAppInfo> scannedAppsMap = MyAppInfo.getPackageNameMapFromList(installedApps);

        Set<String> newPackages = new HashSet<>(scannedAppsMap.keySet());
        newPackages.removeAll(dbAppsMap.keySet());
        Log.d(LOG_TAG, " ");
        Log.d(LOG_TAG, "MyApplication.updateAppsDB: new packages cnt: " + newPackages.size());
        for (String packageName : newPackages) {
            final MyAppInfo scannedAppInfo = scannedAppsMap.get(packageName);
            final AppEntity entity = AppEntity.fromMyAppInfo(scannedAppInfo);
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
            final MyAppInfo myAppInfo = scannedAppsMap.get(packageName);
            final AppEntity appEntity = dbAppsMap.get(packageName);
            myAppInfo.launchedCount = appEntity.launchedCount;
            myAppInfo.lastLaunched = appEntity.lastLaunched;
        }
    }

    public final void insertNewPackage(@NonNull String packageName) {
        try {
            final ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            final MyAppInfo myAppInfo = new MyAppInfo(ai, pm);
            installedApps.add(myAppInfo);
            db.appEntityDao().insert(AppEntity.fromMyAppInfo(myAppInfo));
            sortInstalledApps();
        } catch (PackageManager.NameNotFoundException e) {
            String msg = "MyApplication.insertNewPackage: packageName not found exception";
            Log.e(LOG_TAG, msg);
        }
    }

    public final void deletePackage(@NonNull String packageName) {
        // todo: delete inplace in Dao
        AppEntity toDelete = db.appEntityDao().getAppByPackageName(packageName);
        db.appEntityDao().delete(toDelete);
        for (int i = 0; i < installedApps.size(); ++i) {
            if (installedApps.get(i).packageName.equals(packageName)) {
                installedApps.remove(i);
                return;
            }
        }
        sortInstalledApps();
    }

    public final void increaseAppLaunchedCount(@NonNull String packageName, int installedAppsPosition) {
        // todo: update inplace in Dao
        AppEntity toUpdate = db.appEntityDao().getAppByPackageName(packageName);
        Date date = new Date();
        toUpdate.lastLaunched = date;
        toUpdate.launchedCount++;
        db.appEntityDao().update(toUpdate);
        if (installedAppsPosition < 0 || installedAppsPosition >= installedApps.size()) {
            // TODO: is Log useful when followed by exception throw ?
            final String msg = "MyApplication.increaseAppLaunchedCount: invalid installedAppsPosition passed";
            Log.e(LOG_TAG, msg);
            throw new InvalidParameterException(msg);
        }
        final MyAppInfo myAppInfo = installedApps.get(installedAppsPosition);
        myAppInfo.lastLaunched = date;
        myAppInfo.launchedCount++;
        sortInstalledApps();
    }

    public final int getInstalledAppsCount() {
        return installedApps.size();
    }

    public final MyAppInfo getAppInfo(int i) {
        if (i < 0 || i >= installedApps.size()) {
            final String msg = "MyApplication.getAppInfo: invalid array position passed";
            Log.e(LOG_TAG, msg);
            throw new IndexOutOfBoundsException(msg);
        }
        return installedApps.get(i);
    }

    private void initExternalTrackingServices() {
        Fabric.with(this, new Crashlytics());

        AppCenter.start(this, APP_CENTER_KEY, Distribute.class);

        // Create extended configuration library for AppMetrica
        YandexMetricaConfig config = YandexMetricaConfig
                .newConfigBuilder(APP_METRICA_API_KEY).build();
        // initialize AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // app lifecycle tracking
        YandexMetrica.enableActivityAutoTracking(this);

        YandexMetricaPush.init(getApplicationContext());
    }

    private void getDataFromSharedPreferences() {
        showWelcomePageKey = getString(R.string.showWelcomePageKey);
        showWelcomePage = sharedPreferences.getBoolean(showWelcomePageKey, true);

        nightModeEnabledKey = getString(R.string.sharedPrefNightModeEnabledKey);
        nightModeEnabled = sharedPreferences.getBoolean(nightModeEnabledKey, false);

        compactLayoutEnabledKey = getString(R.string.sharedPrefCompactLayoutEnabledKey);
        compactLayoutEnabled = sharedPreferences.getBoolean(compactLayoutEnabledKey, false);
        currentLayout = compactLayoutEnabled ? LayoutInfo.COMPACT : LayoutInfo.STANDARD;

        karmaKey = getString(R.string.sharedPrefKarmaKey);
        karmaValue = sharedPreferences.getInt(karmaKey, 0);

        karmaLastChangeDateKey = getString(R.string.sharedPrefKarmaLastChangeDateKey);
        karmaLastChangeDateValue = sharedPreferences.getLong(karmaLastChangeDateKey, 0);
        if (karmaLastChangeDateValue == 0) {
            karmaLastChangeDateValue = null;
        }

        isSortAscendingKey = getString(R.string.sharedPrefIsSortAscendingKey);
        isSortAscending = sharedPreferences.getBoolean(isSortAscendingKey, false);

        SORT_MODE_NAME = getString(R.string.sharedPrefSortModeName);
        SORT_MODE_FREQUENCY = getString(R.string.sharedPrefSortModeFrequency);
        SORT_MODE_INSTALLATION_DATE = getString(R.string.sharedPrefSortModeInstallationDate);
        SORT_MODE_NO_SORT = getString(R.string.sharedPrefSortModeNoSort);
        sortModeKey = getString(R.string.sharedPrefSortModeKey);
        sortMode = sharedPreferences.getString(sortModeKey, SORT_MODE_NO_SORT);

        LANGUAGE_ENGLISH = getString(R.string.sharedPrefLanguageEnglish);
        LANGUAGE_BELARUSIAN = getString(R.string.sharedPrefLanguageBelarusian);
        LANGUAGE_RUSSIAN = getString(R.string.sharedPrefLanguageRussian);
        LANGUAGE_SYSTEM_DEFAULT = getString(R.string.sharedPrefLanguageSystemDefault);
        languageKey = getString(R.string.sharedPrefLanguageKey);
        language = sharedPreferences.getString(languageKey, LANGUAGE_SYSTEM_DEFAULT);
    }

    private void syncAppTheme() {
        if (nightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public final boolean isShowWelcomePage() {
        return showWelcomePage;
    }

    public final void setShowWelcomePage(boolean value) {
        showWelcomePage = value;
        sharedPreferences
                .edit()
                .putBoolean(showWelcomePageKey, showWelcomePage)
                .apply();

        Log.d(LOG_TAG, "showWelcomePage: " + value);
        YandexMetrica.reportEvent("setting show welcome page on next run to " + value);
    }

    public final boolean isNighModeEnabled() {
        return nightModeEnabled;
    }

    public final void setNightModeEnabled(boolean value) {
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

    public final boolean isCompactLayoutEnabled() {
        return compactLayoutEnabled;
    }

    public final void setCompactLayoutEnabled(boolean value) {
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

    public int getKarmaValue() {
        return karmaValue;
    }

    public void setKarmaValue(int karmaValue) {
        this.karmaValue = karmaValue;
        sharedPreferences
                .edit()
                .putInt(karmaKey, karmaValue)
                .apply();

        final String msg = "karma updated. karma: " + karmaValue;
        Log.d(MyApplication.LOG_TAG, msg);
    }

    public Date getKarmaLastChangeDate() {
        return DateConverter.toDate(karmaLastChangeDateValue);
    }

    public void setKarmaLastChangeDateToNow() {
        Date now = new Date();
        karmaLastChangeDateValue = DateConverter.toTimestamp(now);
        sharedPreferences.
                edit()
                .putLong(karmaLastChangeDateKey, karmaLastChangeDateValue)
                .apply();
        Log.d(MyApplication.LOG_TAG, "setting last karma change date to now");
    }

    public String getSortMode() {
        return sortMode;
    }

    public void setSortMode(String sortMode) {
        if (sortMode.equals(SORT_MODE_NAME) ||
                sortMode.equals(SORT_MODE_FREQUENCY) ||
                sortMode.equals(SORT_MODE_INSTALLATION_DATE) ||
                sortMode.equals(SORT_MODE_NO_SORT)) {
            this.sortMode = sortMode;
            sortInstalledApps();
            sharedPreferences.
                    edit()
                    .putString(sortModeKey, sortMode)
                    .apply();
            Log.d(MyApplication.LOG_TAG, "setting sort mode to: " + sortMode);
        } else {
            throw new IllegalArgumentException("passed invalid sortMode");
        }
    }

    public boolean isSortAscending() {
        return isSortAscending;
    }

    public void setSortAscending(boolean isSortAscending) {
        this.isSortAscending = isSortAscending;
        sortInstalledApps();
        sharedPreferences.
                edit()
                .putBoolean(isSortAscendingKey, isSortAscending)
                .apply();
        Log.d(MyApplication.LOG_TAG, "setting sort ascending to: " + isSortAscending);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if (language.equals(LANGUAGE_ENGLISH) ||
                language.equals(LANGUAGE_BELARUSIAN) ||
                language.equals(LANGUAGE_RUSSIAN) ||
                language.equals(LANGUAGE_SYSTEM_DEFAULT)) {
            this.language = language;
            if (activityLauncher != null) {
                activityLauncher.updateLocale();
            }
            sharedPreferences.
                    edit()
                    .putString(languageKey, language)
                    .apply();
            Log.d(MyApplication.LOG_TAG, "setting language to: " + language);
        } else {
            throw new IllegalArgumentException("passed invalid language");
        }
    }

    private void registerNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.notificationChannelName);
            String description = getString(R.string.notificationChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.notificationChannelId), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initIsDefaultSystemLauncher() {
        isDefaultSystemLauncher = false;

        List<IntentFilter> filters = new ArrayList<>();
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filters.add(filter);

        List<ComponentName> activities = new ArrayList<>();
        pm.getPreferredActivities(filters, activities, myPackageName);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                isDefaultSystemLauncher = true;
            }
        }

        final String msg = isDefaultSystemLauncher
                ? "this app IS SET as default system launcher"
                : "this app IS NOT SET as system default launcher";
        Log.w(LOG_TAG, msg);
    }

    public boolean isDefaultSystemLauncher() {
        return isDefaultSystemLauncher;
    }
}
