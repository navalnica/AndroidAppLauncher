package com.example.trafimau_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: remove listener
        findViewById(R.id.mainAuthorImage).setOnLongClickListener(v -> {
            Intent intent = new Intent(this, WelcomePageStart.class);
            startActivity(intent);
            return true;
        });

        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        drawerLayout = findViewById(R.id.mainActivityDrawerLayout);

        NavigationView navigationView = findViewById(R.id.mainActivityNavigationView);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    drawerLayout.closeDrawers();
                    Intent intent;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_launcher_activity:
                            intent = new Intent(this, LauncherActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_list_activity:
                            intent = new Intent(this, ListActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            // TODO: create Settings activity!
                            final String msg = "not implemented nav bar item with title: "
                                    + menuItem.getTitle();
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }

                    return true;
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
