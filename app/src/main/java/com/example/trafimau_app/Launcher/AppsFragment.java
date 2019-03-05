package com.example.trafimau_app.Launcher;

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
    public static final String ARG_APP_PAGE_INDEX = "page_index";

    private ViewPager viewPager;
    private AppsViewPagerAdapter pagerAdapter;

    public static AppsFragment newInstance(int activePageIndex) {
        AppsFragment fragment = new AppsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APP_PAGE_INDEX, activePageIndex);
        fragment.setArguments(args);
        return fragment;
    }

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

        final Bundle arguments = getArguments();
        if (arguments != null) {
            int pageIndex = arguments.getInt(ARG_APP_PAGE_INDEX);
            viewPager.setCurrentItem(pageIndex);
        }

        YandexMetrica.reportEvent("AppsFragment.onCreateView");

        return rootView;
    }

    public void setCurrentPage(int pageIndex) {
        if(pageIndex < 0 || pageIndex >= 3){
            Log.d(MyApplication.LOG_TAG, "AppsFragment.setCurrentItem: page index is invalid");
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
