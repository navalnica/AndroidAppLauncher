package com.example.trafimau_app;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class LauncherActivity extends AppCompatActivity {

    private MyApplication app;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        MyApplication app = (MyApplication) getApplication();
        drawerLayout = findViewById(R.id.launcherDrawerLayout);

        configureToolbar();
        configureNavigationDrawer();
        inflateFragment(new ListFragment());
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
                            inflateFragment(new GridFragment());
                            break;
                        case R.id.navigationListFragment:
                            inflateFragment(new ListFragment());
                            break;
                        case R.id.navigationSettingsFragment:
                        default:
                            final String msg = menuItem.getTitle() + " fragment not implemented";
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                    return true;
                });
    }

    private void inflateFragment(Fragment fragment) {
        // TODO: do not add identical fragment. Maybe SingleTop model could help?
        // TODO: do not add first fragment to BackStack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.launcherFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
