package com.example.trafimau_app.activity.launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.yandex.metrica.YandexMetrica;

public class FragmentPreferences
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Activity activity;
    private MyApplication app;

    private String keyDarkThemeSwitch;
    private String keyLayoutList;
    private String keyShowWelcomePageCheckbox;

    private SwitchPreference darkThemeSwitch;
    private ListPreference layoutListPreference;
    private String[] layoutDescriptions;
    private CheckBoxPreference showWelcomePageCheckbox;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);

        layoutDescriptions = getResources().getStringArray(R.array.layout_descriptions);

        activity = getActivity();
        if (activity == null) {
            final String msg = "FragmentPreferences: getActivity() returned null";
            Log.d(MyApplication.LOG_TAG, msg);
            YandexMetrica.reportEvent(msg);
            throw new NullPointerException(msg);
        }
        app = (MyApplication) activity.getApplication();

        keyDarkThemeSwitch = getResources().getString(R.string.prefKeyDarkThemeSwitch);
        keyLayoutList = getResources().getString(R.string.prefKeyLayoutListPreference);
        keyShowWelcomePageCheckbox = getResources().getString(R.string.prefKeyShowWelcomePageCheckbox);

        darkThemeSwitch = (SwitchPreference) findPreference(keyDarkThemeSwitch);
        layoutListPreference = (ListPreference) findPreference(keyLayoutList);
        showWelcomePageCheckbox = (CheckBoxPreference) findPreference(keyShowWelcomePageCheckbox);

        initializeViews();

        // TODO: handle checkbox status on device rotation
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(MyApplication.LOG_TAG, "FragmentPreferences.onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(MyApplication.LOG_TAG, "FragmentPreferences.onSaveInstanceState");
    }

    private void initializeViews() {
        darkThemeSwitch.setChecked(app.isNighModeEnabled());
        showWelcomePageCheckbox.setChecked(app.isShowWelcomePage());
        layoutListPreference.setValue(Boolean.toString(app.isCompactLayoutEnabled()));
        setSummaryForLayoutListPreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        // update summary for currentLayout picker
        if (key.equals(keyLayoutList)) {
            final String value = layoutListPreference.getValue();
            boolean compactModeEnabled = Boolean.valueOf(value);
            app.setCompactLayoutEnabled(compactModeEnabled);
            setSummaryForLayoutListPreference();
        } else if (key.equals(keyDarkThemeSwitch)) {
            app.setNightModeEnabled(darkThemeSwitch.isChecked());
            YandexMetrica.reportEvent("recreating Launcher Activity");
            activity.recreate();
        } else if (key.equals(keyShowWelcomePageCheckbox)) {
            app.setShowWelcomePage(showWelcomePageCheckbox.isChecked());
        }

    }

    private void setSummaryForLayoutListPreference() {
        int valueIndex = layoutListPreference.findIndexOfValue(
                layoutListPreference.getValue()
        );

        CharSequence summary = layoutListPreference.getEntries()[valueIndex]
                + ":\n\n" + layoutDescriptions[valueIndex];
        layoutListPreference.setSummary(summary);
    }
}