package com.example.trafimau_app;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

// TODO: com.microsoft.appcenter.http.HttpException: 401 - CorrelationId: ...
// ReasonCode: AppSecretDenied

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    private static final String APP_CENTER_KEY = "837acbd8-490f-4613-8c34-8cadf9bd3268";
    private final static String APP_METRICA_API_KEY = "2ade6b48-042d-4406-acbf-d59322bafa72";

    public final static String LOG_TAG = "MyApp";

    public AppsDataModel appsDataModel = new AppsDataModel();
    public DesktopSiteLinksDataModel sitesDataModel = new DesktopSiteLinksDataModel();

    private SharedPreferences sharedPreferences;

    private String showWelcomePageKey;
    private boolean showWelcomePage = false;

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
        initExternalTrackingServicies();
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

    public void syncAppTheme() {
        if (nightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        YandexMetrica.reportEvent("MyApplication: syncing app theme");
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
