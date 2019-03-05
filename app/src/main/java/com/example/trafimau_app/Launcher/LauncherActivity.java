package com.example.trafimau_app.Launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

import java.util.Stack;

public class LauncherActivity extends AppCompatActivity {

    private MyApplication app;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean profileFragmentActive = false;

    private String appsTitle;
    private String settingsTitle;

    // needed to avoid inflating the same fragment
    private Fragment initialFragment;
    private boolean initialFragmentInflated = false;

    private AppsFragment curAppsFragment;
    private int curAppsFragmentPageIndex;

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
                app.savePackageToDB(packageName);
                recreate();
            }
        }
    };

    private class FragmentsStackEntry {
        public boolean isAppsFragment;
        public AppsFragment fragment;
        public int pageIndex;

        public FragmentsStackEntry(boolean isAppsFragment, AppsFragment fragment, int pageIndex) {
            this.isAppsFragment = isAppsFragment;
            this.fragment = fragment;
            this.pageIndex = pageIndex;
        }
    }

    private final Stack<FragmentsStackEntry> fragmentsStack = new Stack<>();
    private final Stack<MenuItem> navMenuItemsStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        app = (MyApplication) getApplication();

        drawerLayout = findViewById(R.id.launcherDrawerLayout);
        navigationView = findViewById(R.id.navigationDrawer);
        navMenuItemsStack.push(navigationView.getCheckedItem());

        appsTitle = getString(R.string.applications);
        settingsTitle = getString(R.string.settings);

        if (savedInstanceState == null) {
            curAppsFragmentPageIndex = AppsFragment.Page.DESKTOP.ordinal();
            curAppsFragment = AppsFragment.newInstance(curAppsFragmentPageIndex);
            initialFragment = curAppsFragment;

            inflateFragment(initialFragment, false, appsTitle);
            initialFragmentInflated = true;
        }

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

        // TODO: try to simplify with
//         override fun onSupportNavigateUp(): Boolean {
//         if (supportFragmentManager.popBackStackImmediate()) {
//             return true
//         }
//         return super.onSupportNavigateUp()
//     }

        Log.d(MyApplication.LOG_TAG, "LauncherActivity.onBackPressed");

        // restore previously selected drawer item
        if (!navMenuItemsStack.empty()) {
            navMenuItemsStack.pop();
            if (!navMenuItemsStack.empty()) {
                navMenuItemsStack.peek().setChecked(true);
            }
        }

        // refresh flag
        if (profileFragmentActive) {
            profileFragmentActive = false;
        }

        // TODO: is it ok not to call super.onBackPressed() ?
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (fragmentsStack.size() > 1) {
            FragmentsStackEntry top = fragmentsStack.pop();
            FragmentsStackEntry newTopEntry = fragmentsStack.peek();
            if (newTopEntry.isAppsFragment) {
                curAppsFragment = newTopEntry.fragment;
                curAppsFragmentPageIndex = newTopEntry.pageIndex;
                curAppsFragment.setCurrentPage(curAppsFragmentPageIndex);
            }
            if (top.isAppsFragment && newTopEntry.isAppsFragment) {
                // we have set previous page in ViewPager.
                // no need to process backstack because we did not use it here.
                // all sites is stored in separate fragmentsStack
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(MyApplication.LOG_TAG, "LauncherActivity.onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            // if ProfileFragment is active than we do not need to open drawer
            // just return to previous fragment
            if (profileFragmentActive) {
                profileFragmentActive = false;
                return super.onOptionsItemSelected(item);
            }
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
                    menuItem.setChecked(true);
                    navMenuItemsStack.push(menuItem);
                    if (menuItem.getItemId() == R.id.navPreferencesFragment) {
                        inflateFragment(new PreferencesFragment(), true, settingsTitle);
                    } else {
                        Fragment appsFragment;
                        AppsFragment.Page page;
                        switch (menuItem.getItemId()) {
                            case R.id.navDesktopFragment:
                                page = AppsFragment.Page.DESKTOP;
                                break;
                            case R.id.navGridFragment:
                                page = AppsFragment.Page.GRID;
                                break;
                            case R.id.navListFragment:
                            default:
                                page = AppsFragment.Page.LIST;
                                break;
                        }
                        appsFragment = AppsFragment.newInstance(page.ordinal());
                        inflateFragment(appsFragment, true, appsTitle);
                    }
                    drawerLayout.closeDrawers();
                    return true;
                });

        navigationView.getHeaderView(0).findViewById(R.id.navDrawerAuthorImage)
                .setOnClickListener(view -> {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    profileFragmentActive = true;
                    inflateFragment(new ProfileFragment(), true, null);
                });
    }

    private void inflateFragment(Fragment fragment, boolean addToBackStack, String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        final int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        final String notInflatingMsg =
                "LauncherActivity.inflateFragment: not inflating. fragment already exists";

        // check if new fragment is the same as the initial one
        if (backStackEntryCount == 0 && initialFragmentInflated) {
            if (fragment.getClass().getSimpleName().equals(
                    initialFragment.getClass().getSimpleName())) {

                Log.d(MyApplication.LOG_TAG, notInflatingMsg);
                pushToFragmentsStack(fragment, true);
                return;
            }
        }
        // check if new fragment is the same as previous
        else if (backStackEntryCount > 0) {
            final FragmentManager.BackStackEntry topFragment =
                    fragmentManager.getBackStackEntryAt(backStackEntryCount - 1);
            String topFragmentName = topFragment.getName();
            if (fragment.getClass().getSimpleName().equals(topFragmentName)) {

                Log.d(MyApplication.LOG_TAG, notInflatingMsg);
                pushToFragmentsStack(fragment, true);
                return;
            }
        }

        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.launcherFragmentContainer, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();

        pushToFragmentsStack(fragment, false);

        if (title != null) {
            setTitle(title);
        }
    }

    private void pushToFragmentsStack(Fragment fragment, boolean updatingExistingView) {
        if (fragment instanceof AppsFragment) {
            final Bundle arguments = fragment.getArguments();
            if (arguments != null) {
                int newPageIndex = arguments.getInt(AppsFragment.ARG_APP_PAGE_INDEX);
                if (updatingExistingView) {
                    if (newPageIndex != curAppsFragmentPageIndex) {
                        fragmentsStack.push(new FragmentsStackEntry(
                                true, curAppsFragment, newPageIndex));

                        curAppsFragmentPageIndex = newPageIndex;
                        curAppsFragment.setCurrentPage(curAppsFragmentPageIndex);
                    }
                } else {
                    fragmentsStack.push(new FragmentsStackEntry(
                            true, (AppsFragment) fragment, newPageIndex));
                    curAppsFragment = (AppsFragment) fragment;
                    curAppsFragmentPageIndex = newPageIndex;
                }
            }
        } else {
            fragmentsStack.push(new FragmentsStackEntry(false, null, -1));
        }
    }
}
