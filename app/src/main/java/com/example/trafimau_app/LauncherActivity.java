package com.example.trafimau_app;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class LauncherActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        drawerLayout = findViewById(R.id.launcherDrawerLayout);

        configureToolbar();
        configureNavigationDrawer();

        if(savedInstanceState == null){
            inflateFragment(new ListFragment(), false);
            setTitle(R.string.list_launcher);
        }
    }

    // TODO : remove
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
                        default:
                            final String msg = menuItem.getTitle() + " fragment not implemented";
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                    return true;
                });
    }

    private void inflateFragment(Fragment fragment, boolean addToBackStack) {
        // TODO: do not add identical fragment. Maybe SingleTop model could help?
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.launcherFragmentContainer, fragment);
        if(addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
