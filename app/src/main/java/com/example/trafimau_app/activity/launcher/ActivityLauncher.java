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

public class ActivityLauncher extends AppCompatActivity {

    private MyApplication app;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private boolean isAppsFragmentShown = true;
    private AppsFragment appsFragment;

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
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: save view pager selected item index and restore it
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        app = (MyApplication) getApplication();
        fragmentManager = getSupportFragmentManager();

        drawerLayout = findViewById(R.id.launcherDrawerLayout);
        navigationView = findViewById(R.id.navigationDrawer);

        appsFragment = new AppsFragment();
        inflateFragment(appsFragment, false);
        setTitle(R.string.applications);
        isAppsFragmentShown = true;

        configureToolbar();
        configureNavigationDrawer();
        initAppsBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appsBroadcastReceiver);
    }

    private void initAppsBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(appsBroadcastReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (!isAppsFragmentShown) {
            isAppsFragmentShown = true;
            setTitle(R.string.applications);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.launcherToolbar);
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
                            popSettingsAndShowApps();
                            appsFragment.setCurrentPage(AppsFragment.Page.DESKTOP);
                            break;
                        case R.id.navGridFragment:
                            popSettingsAndShowApps();
                            appsFragment.setCurrentPage(AppsFragment.Page.GRID);
                            break;
                        case R.id.navListFragment:
                            popSettingsAndShowApps();
                            appsFragment.setCurrentPage(AppsFragment.Page.LIST);
                            break;
                        case R.id.navPreferencesFragment:
                            inflateFragment(new PreferencesFragment(), true);
                            setTitle(R.string.settings);
                            isAppsFragmentShown = false;
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

    private void popSettingsAndShowApps() {
        if (!isAppsFragmentShown) {
            fragmentManager.popBackStackImmediate();
            isAppsFragmentShown = true;
            setTitle(R.string.applications);
        }
    }

    private void inflateFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.launcherFragmentContainer, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
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
}
