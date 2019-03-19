package com.example.trafimau_app.activity.launcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.yandex.metrica.YandexMetrica;

public class AppsFragment extends Fragment {

    private ViewPager viewPager;
    private AppsViewPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_apps, container, false);
        viewPager = rootView.findViewById(R.id.appsViewPager);

        // it is very important to use getChildFragmentManager()
        // instead getActivity().getSupportFragmentManager()
        pagerAdapter = new AppsViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        YandexMetrica.reportEvent("AppsFragment.onCreateView");

        return rootView;
    }

    public void setCurrentPage(Page page) {
        int pageIndex = page.ordinal();
        if(pageIndex < 0 || pageIndex >= 3){
            Log.e(MyApplication.LOG_TAG, "AppsFragment.setCurrentItem: page index is invalid");
            pageIndex = 0;
        }
        if (viewPager != null) {
            viewPager.setCurrentItem(pageIndex);
        } else {
            Log.d(MyApplication.LOG_TAG, "AppsFragment.setCurrentItem: viewPager == null");
        }
    }

    private class AppsViewPagerAdapter extends FragmentStatePagerAdapter {

        public AppsViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == Page.GRID.ordinal()) {
                return new GridFragment();
            } else if (position == Page.LIST.ordinal()) {
                return new ListFragment();
            } else {
                return new DesktopFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public enum Page {
        DESKTOP, GRID, LIST
    }

}
