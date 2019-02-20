package com.example.trafimau_app;

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
import android.view.MenuItem;

public class LauncherActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private boolean initialFragmentInflated = false;
    private Fragment initialFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        drawerLayout = findViewById(R.id.launcherDrawerLayout);

        configureToolbar();
        configureNavigationDrawer();

        if(savedInstanceState == null){
            // TODO: handle saved instance state

            initialFragment = new ListFragment();
            inflateFragment(initialFragment, false);
            initialFragmentInflated = true;
            if(initialFragment instanceof ListFragment){
                setTitle(R.string.list_launcher);
            }
            else if (initialFragment instanceof GridFragment){
                setTitle(R.string.grid_launcher);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
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

    private void configureNavigationDrawer(){
        NavigationView navigationView = findViewById(R.id.navigationDrawer);

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    drawerLayout.closeDrawers();

                    switch (menuItem.getItemId()) {
                        case R.id.navigationGridFragment:
                            inflateFragment(new GridFragment(), true);
                            setTitle(R.string.grid_launcher);
                            break;
                        case R.id.navigationListFragment:
                            inflateFragment(new ListFragment(), true);
                            setTitle(R.string.list_launcher);
                            break;
                        case R.id.navigationSettingsFragment:
                            inflateFragment(new SettingsFragment(), true);
                            setTitle(R.string.settings);
                            break;
                    }
                    return true;
                });

        navigationView.getHeaderView(0).findViewById(R.id.navDrawerAuthorImage)
                .setOnClickListener(view -> {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    inflateFragment(new ProfileFragment(), true);
                });
    }

    private void inflateFragment(Fragment fragment, boolean addToBackStack) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        final int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if(backStackEntryCount == 0 && initialFragmentInflated){
            if(fragment.getClass().getSimpleName().equals(
                    initialFragment.getClass().getSimpleName())){
                return;
            }
        }
        else if (backStackEntryCount > 0){
            String topFragmentName = fragmentManager.getBackStackEntryAt(
                    backStackEntryCount - 1).getName();
            if(fragment.getClass().getSimpleName().equals(topFragmentName)){
                return;
            }
        }

        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.launcherFragmentContainer, fragment);
        if(addToBackStack){
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }
}
