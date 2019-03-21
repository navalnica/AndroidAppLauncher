package com.example.trafimau_app.activity.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.MenuItem;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.example.trafimau_app.activity.ActivityProfile;
import com.example.trafimau_app.activity.PreferencesFragment;

public class ActivityLauncher extends AppCompatActivity
        implements AppsFragment.PageChangedListener {

    private MyApplication app;
    private AppsFragment appsFragment;
    private FragmentManager fragmentManager;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private int currentTitleResId;
    private final String currentTitleResIdKey = "current_title_res_id";

    private boolean isSettingsFragmentVisible;
    private final String isSettingsFragmentVisibleKey = "is_settings_fragment_visible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MyApplication.LOG_TAG, "ActivityLauncher.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        app = (MyApplication) getApplication();
        fragmentManager = getSupportFragmentManager();

        drawerLayout = findViewById(R.id.launcherDrawerLayout);
        navigationView = findViewById(R.id.navigationDrawer);
        toolbar = findViewById(R.id.launcherToolbar);

        Log.d(MyApplication.LOG_TAG, "ActivityLauncher: savedInstanceState: " + savedInstanceState);
        if (savedInstanceState == null) {
            appsFragment = new AppsFragment();

            inflateFragment(appsFragment, false);
            isSettingsFragmentVisible = false;
            setCurrentTitle(R.string.desktop);
        }else{
            Fragment fragment = fragmentManager.findFragmentByTag(AppsFragment.class.getSimpleName());
            if(fragment instanceof AppsFragment){
                appsFragment = (AppsFragment) fragment;
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
        }
        super.onBackPressed();
    }

    @Override
    public void onPageChanged(AppsFragment.Page page) {
        if(page == null){
            currentTitleResId = R.string.app_name;
        }else if (page == AppsFragment.Page.DESKTOP){
            currentTitleResId = R.string.desktop;
        }else if(page == AppsFragment.Page.LIST || page == AppsFragment.Page.GRID){
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
                            appsFragment.setCurrentPage(AppsFragment.Page.DESKTOP);
                            setCurrentTitle(R.string.desktop);
                            break;
                        case R.id.navGridFragment:
                            popSettingsIfActiveAndShowApps();
                            appsFragment.setCurrentPage(AppsFragment.Page.GRID);
                            setCurrentTitle(R.string.applications);
                            break;
                        case R.id.navListFragment:
                            popSettingsIfActiveAndShowApps();
                            appsFragment.setCurrentPage(AppsFragment.Page.LIST);
                            setCurrentTitle(R.string.applications);
                            break;
                        case R.id.navPreferencesFragment:
                            inflateFragment(new PreferencesFragment(), true);
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

    // TODO: move registration from activity to application
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

            // TODO: do not recreate activity. consider using notifyDatasetChanged or LiveData
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
                    action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                app.deletePackageFromDB(packageName);
                recreate();
            } else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                app.insertPackageToDB(packageName);
                recreate();
            }
        }
    };
}
