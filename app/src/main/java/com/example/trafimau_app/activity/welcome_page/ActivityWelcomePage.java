package com.example.trafimau_app.activity.welcome_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.example.trafimau_app.activity.launcher.ActivityLauncher;
import com.yandex.metrica.YandexMetrica;

public class ActivityWelcomePage extends AppCompatActivity {

    private ViewPager viewPager;
    private int FRAGMENTS_COUNT = 4;
    private MyApplication app;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        app = (MyApplication) getApplication();

        if (app.isShowWelcomePage()) {
            Log.d(MyApplication.LOG_TAG, "isShowWelcomePage: true");
        } else {
            Log.d(MyApplication.LOG_TAG, "isShowWelcomePage: false");
            Log.d(MyApplication.LOG_TAG, "starting Launcher Activity");
            Intent intent = new Intent(this, ActivityLauncher.class);
            startActivity(intent);
            finish();
        }

        viewPager = findViewById(R.id.welcomePageViewPager);
        WelcomePageViewPagerAdapter adapter = new WelcomePageViewPagerAdapter(
                getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i < FRAGMENTS_COUNT - 1) {
                    continueButton.setText(R.string.buttonContinue);
                } else {
                    continueButton.setText(R.string.buttonFinish);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        continueButton = findViewById(R.id.welcomeContinueButton);
        continueButton.setOnClickListener(this::onContinueButtonClick);

        TabLayout tabLayout = findViewById(R.id.welcomeTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        YandexMetrica.reportEvent("Showing Welcome Page");
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

    public void onContinueButtonClick(View view) {
        final int currentItem = viewPager.getCurrentItem();
        if (currentItem < FRAGMENTS_COUNT - 1) {
            viewPager.setCurrentItem(currentItem + 1);
        } else {
            Log.d(MyApplication.LOG_TAG, "settings showWelcomePage to false");
            app.setShowWelcomePage(false);

            Intent intent = new Intent(this, ActivityLauncher.class);
            startActivity(intent);
            finish();
        }
    }

    private class WelcomePageViewPagerAdapter extends FragmentStatePagerAdapter {
        public WelcomePageViewPagerAdapter(FragmentManager fm) {
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
