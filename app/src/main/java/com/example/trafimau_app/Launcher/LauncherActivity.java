package com.example.trafimau_app.Launcher;

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
    private boolean profileFragmentResumed = false;
    private Fragment initialFragment;
    private boolean initialFragmentInflated = false;
    private final Stack<MenuItem> navMenuItemsStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        drawerLayout = findViewById(R.id.launcherDrawerLayout);
        navigationView = findViewById(R.id.navigationDrawer);
        navMenuItemsStack.push(navigationView.getCheckedItem());

        if (savedInstanceState == null) {
            initialFragment = new GridFragment();
            inflateFragment(initialFragment, false);
            initialFragmentInflated = true;
            if (initialFragment instanceof ListFragment ||
                    initialFragment instanceof GridFragment) {
                setTitle(R.string.launcherTitle);
            }
            // TODO: handle title for Desktop fragment
        }

        configureToolbar();
        configureNavigationDrawer();

        app = (MyApplication) getApplication();
        app.dataModel.syncWithInstalledAppsInfo(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(MyApplication.LOG_TAG, "LauncherActivity.onBackPressed");

        if (!navMenuItemsStack.empty()) {
            navMenuItemsStack.pop();
            navMenuItemsStack.peek().setChecked(true);
        }

        if (profileFragmentResumed) {
            // refresh flag
            profileFragmentResumed = false;
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(MyApplication.LOG_TAG, "LauncherActivity.onOptionsItemSelected");
        if (item.getItemId() == android.R.id.home) {
            // use flag to choose whether to open drawer or not
            if (profileFragmentResumed) {
                profileFragmentResumed = false;
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

                    switch (menuItem.getItemId()) {
                        case R.id.navGridFragment:
                            inflateFragment(new GridFragment(), true);
                            setTitle(R.string.launcherTitle);
                            break;
                        case R.id.navListFragment:
                            inflateFragment(new ListFragment(), true);
                            setTitle(R.string.launcherTitle);
                            break;
                        case R.id.navDesktopFragment:
                            // TODO
                            throw new UnsupportedOperationException(
                                    "Desktop fragment is not implemented yet");
                        case R.id.navPreferencesFragment:
                            inflateFragment(new PreferencesFragment(), true);
                            setTitle(R.string.settings);
                            break;
                    }

                    drawerLayout.closeDrawers();
                    return true;
                });

        navigationView.getHeaderView(0).findViewById(R.id.navDrawerAuthorImage)
                .setOnClickListener(view -> {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    profileFragmentResumed = true;
                    inflateFragment(new ProfileFragment(), true);
                });
    }

    private void inflateFragment(Fragment fragment, boolean addToBackStack) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        final int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        // check if new fragment is the same as the initial one
        if (backStackEntryCount == 0 && initialFragmentInflated) {
            if (fragment.getClass().getSimpleName().equals(
                    initialFragment.getClass().getSimpleName())) {
                return;
            }
        }
        // check if new fragment is the same as previous
        else if (backStackEntryCount > 0) {
            String topFragmentName = fragmentManager.getBackStackEntryAt(
                    backStackEntryCount - 1).getName();
            if (fragment.getClass().getSimpleName().equals(topFragmentName)) {
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
    }
}
