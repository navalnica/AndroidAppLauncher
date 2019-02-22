package com.example.trafimau_app.WelcomePage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.trafimau_app.Launcher.LauncherActivity;
import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class WelcomePageActivity extends AppCompatActivity implements
        OnContinueButtonClickListener, ThemeChangedListener {

    private ViewPager viewPager;
    private int FRAGMENTS_COUNT = 4;
    private MyApplication app;

    // TODO: add tabs with fragment titles
    // TODO: check if WelcomePage should be launched

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        app = (MyApplication) getApplication();

        viewPager = findViewById(R.id.welcomePageViewPager);
        WelcomePagerViewPagerAdapter adapter = new WelcomePagerViewPagerAdapter(
                getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        final int currentItem = viewPager.getCurrentItem();
        if (currentItem == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(currentItem - 1);
        }
    }

    @Override
    public void onContinueButtonClick(View view) {
        final int currentItem = viewPager.getCurrentItem();
        if (currentItem < FRAGMENTS_COUNT - 1) {
            viewPager.setCurrentItem(currentItem + 1);
        } else {
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void resetTheme() {
        Log.d(MyApplication.LOG_TAG, "WelcomePageActivity: resetTheme()");

        app.syncAppTheme();
        recreate();
    }

    private class WelcomePagerViewPagerAdapter extends FragmentStatePagerAdapter {
        public WelcomePagerViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new GreetingFragment();
                case 1:
                    return new AppDescriptionFragment();
                case 2:
                    return new ThemePickerFragment();
                case 3:
                default:
                    return new LayoutPickerFragment();
            }
        }

        @Override
        public int getCount() {
            return FRAGMENTS_COUNT;
        }
    }
}
