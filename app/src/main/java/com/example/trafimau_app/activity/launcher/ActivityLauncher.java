package com.example.trafimau_app.activity.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.example.trafimau_app.activity.ActivityProfile;
import com.example.trafimau_app.data.MyAppInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ActivityLauncher extends AppCompatActivity
        implements FragmentLauncher.PageChangedListener {

    private MyApplication app;
    private FragmentLauncher fragmentLauncher;
    private FragmentManager fragmentManager;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private int currentTitleResId;
    private final String currentTitleResIdKey = "current_title_res_id";

    private boolean isSettingsFragmentVisible;
    private final String isSettingsFragmentVisibleKey = "is_settings_fragment_visible";

    private MyAppInfo selectedItemAppInfo;

    private final List<LauncherAppAdapter> recyclerViewAdapters = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Log.d(MyApplication.LOG_TAG,
                "ActivityLauncher.onCreate(). savedInstanceState is null: " +
                        (savedInstanceState == null));

        app = (MyApplication) getApplication();
        app.setActivityLauncher(this);
        updateLocale();
        fragmentManager = getSupportFragmentManager();

        drawerLayout = findViewById(R.id.launcherDrawerLayout);
        navigationView = findViewById(R.id.navigationDrawer);
        toolbar = findViewById(R.id.launcherToolbar);

        if (savedInstanceState == null) {
            fragmentLauncher = new FragmentLauncher();
            inflateFragment(fragmentLauncher, false);
            isSettingsFragmentVisible = false;
            setCurrentTitle(R.string.desktop);
        } else {
            Fragment fragment = fragmentManager.findFragmentByTag(FragmentLauncher.class.getSimpleName());
            if (fragment instanceof FragmentLauncher) {
                fragmentLauncher = (FragmentLauncher) fragment;
            }
        }

        configureToolbar();
        configureNavigationDrawer();
        initAndRegisterAppsBroadcastReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(MyApplication.LOG_TAG, "ActivityLauncher.onSaveInstanceState");
        outState.putBoolean(isSettingsFragmentVisibleKey, isSettingsFragmentVisible);
        outState.putInt(currentTitleResIdKey, currentTitleResId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(MyApplication.LOG_TAG, "ActivityLauncher.onRestoreInstanceState");
        isSettingsFragmentVisible = savedInstanceState.getBoolean(isSettingsFragmentVisibleKey);
        setCurrentTitle(savedInstanceState.getInt(currentTitleResIdKey));
    }

    private void setCurrentTitle(int resId) {
        if (resId == 0) {
            Log.e(MyApplication.LOG_TAG, "ActivityLauncher.setCurrentTitle: " +
                    "resId == 0. setting to desktop");
            resId = R.string.desktop;
        }
        currentTitleResId = resId;
        setTitle(currentTitleResId);
    }

    private void initAndRegisterAppsBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        // TODO: use local broadcast manager
        registerReceiver(appsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appsBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (isSettingsFragmentVisible) {
            isSettingsFragmentVisible = false;
            super.onBackPressed();
        }
        else if(!app.isDefaultSystemLauncher()){
            super.onBackPressed();
        }
    }

    @Override
    public void onPageChanged(FragmentLauncher.Page page) {
        if (page == null) {
            currentTitleResId = R.string.app_name;
        } else if (page == FragmentLauncher.Page.DESKTOP) {
            currentTitleResId = R.string.desktop;
        } else if (page == FragmentLauncher.Page.LIST || page == FragmentLauncher.Page.GRID) {
            currentTitleResId = R.string.applications;
        }
        setTitle(currentTitleResId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }

    private void configureNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    final int itemId = menuItem.getItemId();

                    switch (itemId) {
                        case R.id.navDesktopFragment:
                            popSettingsIfActiveAndShowApps();
                            fragmentLauncher.setCurrentPage(FragmentLauncher.Page.DESKTOP);
                            setCurrentTitle(R.string.desktop);
                            break;
                        case R.id.navGridFragment:
                            popSettingsIfActiveAndShowApps();
                            fragmentLauncher.setCurrentPage(FragmentLauncher.Page.GRID);
                            setCurrentTitle(R.string.applications);
                            break;
                        case R.id.navListFragment:
                            popSettingsIfActiveAndShowApps();
                            fragmentLauncher.setCurrentPage(FragmentLauncher.Page.LIST);
                            setCurrentTitle(R.string.applications);
                            break;
                        case R.id.navPreferencesFragment:
                            inflateFragment(new FragmentPreferences(), true);
                            isSettingsFragmentVisible = true;
                            setCurrentTitle(R.string.settings);
                            break;
                        case R.id.sendSimplePush:
                            sendSimplePush();
                            break;
                        case R.id.sendPushWithIcon:
                            sendPushWithColor();
                            break;
                    }
                    drawerLayout.closeDrawers();
                    return true;
                });

        navigationView.getHeaderView(0)
                .findViewById(R.id.navDrawerAuthorImage)
                .setOnClickListener(view -> {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(this, ActivityProfile.class);
                    startActivity(intent);
                });
    }

    private void popSettingsIfActiveAndShowApps() {
        if (isSettingsFragmentVisible) {
            fragmentManager.popBackStackImmediate();
            isSettingsFragmentVisible = false;
        }
    }

    private void inflateFragment(Fragment fragment, boolean addToBackStack) {
        final String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.launcherFragmentContainer, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    public void addAppsChangedListener(LauncherAppAdapter adapter) {
        recyclerViewAdapters.add(adapter);
    }

    public void notifyAppsChanged() {
        for (LauncherAppAdapter a : recyclerViewAdapters) {
            a.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.launcher_context_menu, menu);

        int tag = (int) v.getTag();
        selectedItemAppInfo = app.getAppInfo(tag);
        menu.setHeaderTitle(selectedItemAppInfo.label);
        menu.findItem(R.id.menu_launches).setTitle(
                getString(R.string.launches) + selectedItemAppInfo.launchedCount);
        if (selectedItemAppInfo.isSystemApp) {
            menu.findItem(R.id.menu_delete).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (selectedItemAppInfo == null) {
            throw new NullPointerException(
                    "AppsContainerBaseFragment.onContextItemSelected: selectedItemAppInfo is null");
        }

        Uri packageUri = Uri.parse("package:" + selectedItemAppInfo.packageName);
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent infoIntent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri);
                startActivity(infoIntent);
                return true;
            case R.id.menu_delete:
                Log.d(MyApplication.LOG_TAG, "AppsContainerBaseFragment.onContextItemSelected:" +
                        " deleting app: " + selectedItemAppInfo.label);
                Intent deleteIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                startActivity(deleteIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void sendPushWithColor() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, getString(R.string.notificationChannelId))
                .setSmallIcon(R.drawable.ic_color_lens_black_24dp)
                .setColor(Color.MAGENTA)
                .setContentTitle(getString(R.string.push_with_color_title))
                .setContentText(getString(R.string.push_with_color_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(42, builder.build());
    }

    private void sendSimplePush() {
        // push is not removed after click
        // maybe it's caused by not setting ContentIntent

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, getString(R.string.notificationChannelId))
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(getString(R.string.simple_push_title))
                .setContentText(getString(R.string.simple_push_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(null)
                .setAutoCancel(true);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(12, builder.build());
    }

    public void updateLocale() {
        String language = app.getLanguage();
        if (language.equals(getString(R.string.sharedPrefLanguageSystemDefault))) {
            Log.d(MyApplication.LOG_TAG,
                    "ActivityLauncher.updateLocale: using system default language");
            language = Locale.getDefault().getLanguage();
        }

        Log.d(MyApplication.LOG_TAG, "ActivityLauncher.updateLocale: language: " + language);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        // todo: does not work on API 22 and lower
        final Locale locale = new Locale(language);
        if (configuration.locale.equals(locale)) {
            Log.d(MyApplication.LOG_TAG, "ActivityLauncher.updateLocale: " +
                    "actual locale is already set. not updating");
            return;
        }
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, displayMetrics);
        recreate();
    }

    private BroadcastReceiver appsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            Uri data = intent.getData();
            if (action == null || data == null) {
                String msg = "appsBroadcastReceiver.onReceive: intent action or dataString == null";
                Log.e(MyApplication.LOG_TAG, msg);
                throw new NullPointerException(msg);
            }
            String packageName = intent.getData().getSchemeSpecificPart();

            Log.d(MyApplication.LOG_TAG,
                    "appsBroadcastReceiver.onReceive: package: " + packageName
                            + " action: " + action);

            if (action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
                    action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                app.deletePackage(packageName);
            } else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                app.insertNewPackage(packageName);
            }

            notifyAppsChanged();
        }
    };
}
