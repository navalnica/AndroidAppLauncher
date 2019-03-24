package com.example.trafimau_app.activity.launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
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
    private String keySortAscendingSwitch;
    private String keySortModeList;

    private SwitchPreference darkThemeSwitch;
    private SwitchPreference sortAscendingSwitch;
    private ListPreference layoutListPreference;
    private ListPreference sortModeListPreference;
    private CheckBoxPreference showWelcomePageCheckbox;
    private String[] layoutSummary;
    private String[] sortModeSummary;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);

        layoutSummary = getResources().getStringArray(R.array.summary_layout);
        sortModeSummary = getResources().getStringArray(R.array.summary_sort_mode);

        activity = getActivity();
        if (activity == null) {
            final String msg = "FragmentPreferences: getActivity() returned null";
            Log.d(MyApplication.LOG_TAG, msg);
            YandexMetrica.reportEvent(msg);
            throw new NullPointerException(msg);
        }
        app = (MyApplication) activity.getApplication();

        keyDarkThemeSwitch = getString(R.string.prefKeyDarkThemeSwitch);
        keyLayoutList = getString(R.string.prefKeyLayoutListPreference);
        keyShowWelcomePageCheckbox = getString(R.string.prefKeyShowWelcomePageCheckbox);
        keySortAscendingSwitch = getString(R.string.prefKeySortAscendingSwitch);
        keySortModeList = getString(R.string.prefKeySortModeListPreference);

        darkThemeSwitch = (SwitchPreference) findPreference(keyDarkThemeSwitch);
        layoutListPreference = (ListPreference) findPreference(keyLayoutList);
        showWelcomePageCheckbox = (CheckBoxPreference) findPreference(keyShowWelcomePageCheckbox);
        sortAscendingSwitch = (SwitchPreference) findPreference(keySortAscendingSwitch);
        sortModeListPreference = (ListPreference) findPreference(keySortModeList);

        initializeViews();
    }

    private void initializeViews() {
        darkThemeSwitch.setChecked(app.isNighModeEnabled());
        showWelcomePageCheckbox.setChecked(app.isShowWelcomePage());
        layoutListPreference.setValue(Boolean.toString(app.isCompactLayoutEnabled()));
        setSummaryForLayoutListPreference();

        sortModeListPreference.setValue(app.getSortMode());
        sortAscendingSwitch.setChecked(app.isSortAscending());
        setSummaryForSortMode();
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
        if (key.equals(keyLayoutList)) {
            final String value = layoutListPreference.getValue();
            boolean compactModeEnabled = Boolean.valueOf(value);
            app.setCompactLayoutEnabled(compactModeEnabled);
            setSummaryForLayoutListPreference();
        } else if (key.equals(keyDarkThemeSwitch)) {
            app.setNightModeEnabled(darkThemeSwitch.isChecked());
            activity.recreate();
        } else if (key.equals(keyShowWelcomePageCheckbox)) {
            app.setShowWelcomePage(showWelcomePageCheckbox.isChecked());
        } else if (key.equals(keySortAscendingSwitch)) {
            app.setSortAscending(sortAscendingSwitch.isChecked());
        } else if (key.equals(keySortModeList)) {
            app.setSortMode(sortModeListPreference.getValue());
            setSummaryForSortMode();
        }

    }

    private void setSummaryForLayoutListPreference() {
        int valueIndex = layoutListPreference.findIndexOfValue(
                layoutListPreference.getValue()
        );
        CharSequence summary = layoutListPreference.getEntries()[valueIndex]
                + ":\n\n" + layoutSummary[valueIndex];
        layoutListPreference.setSummary(summary);
    }

    private void setSummaryForSortMode(){
        int valueIndex = sortModeListPreference.findIndexOfValue(
                sortModeListPreference.getValue()
        );
        CharSequence summary = sortModeSummary[valueIndex];
        sortModeListPreference.setSummary(summary);
    }
}