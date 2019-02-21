package com.example.trafimau_app.WelcomePage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.trafimau_app.Launcher.LauncherActivity;
import com.example.trafimau_app.R;

public class WelcomePageActivity extends AppCompatActivity implements
        OnContinueButtonClickListener {

    private ViewPager viewPager;
    private int FRAGMENTS_COUNT = 4;

    // TODO: change other classes to Fragments
    // TODO: add navigation with CONTINUE button click
    // TODO: add current tabs with fragment titles
    // TODO: change title for the last button ot FINISH
    // TODO: add theme switching with night mode
    // TODO: make the whole button block clickable. try to disable radio button and listen for layout
    // TODO: store changes to preferences
    // TODO: check if WelcomePage should be launched

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        viewPager = findViewById(R.id.welcomePageViewPager);
        final WelcomePageViewPagerAdapter adapter = new WelcomePageViewPagerAdapter(
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
        if(currentItem < FRAGMENTS_COUNT - 1){
            viewPager.setCurrentItem(currentItem + 1);
        }
        else{
            Intent intent = new Intent(this, LauncherActivity.class);
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
