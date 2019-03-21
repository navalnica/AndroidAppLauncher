package com.example.trafimau_app.activity.launcher;

import android.content.Context;
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
    private PageChangedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PageChangedListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PageChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MyApplication.LOG_TAG, "AppsFragment.onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(MyApplication.LOG_TAG, "AppsFragment.onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(MyApplication.LOG_TAG, "AppsFragment.onCreateView");

        View rootView = inflater.inflate(
                R.layout.fragment_apps, container, false);
        viewPager = rootView.findViewById(R.id.appsViewPager);

        // it is very important to use getChildFragmentManager()
        // instead getActivity().getSupportFragmentManager()
        pagerAdapter = new AppsViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageScrollStateChanged(int i) {}

            @Override
            public void onPageSelected(int i) {
                listener.onPageChanged(getCurrentPage());
            }
        });

        listener.onPageChanged(getCurrentPage());

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
            Log.e(MyApplication.LOG_TAG, "AppsFragment.setCurrentItem: viewPager == null");
        }
    }

    private Page getCurrentPage(){
        if(viewPager == null){
            Log.e(MyApplication.LOG_TAG, "AppsFragment.getCurrentPage: viewPager is null");
            return null;
        }
        int ix = viewPager.getCurrentItem();
        if(ix == Page.DESKTOP.ordinal()){
            return Page.DESKTOP;
        } else if (ix == Page.GRID.ordinal()){
            return Page.GRID;
        } else if(ix == Page.LIST.ordinal()){
            return Page.LIST;
        }
        Log.e(MyApplication.LOG_TAG,
                "AppsFragment.getCurrentPage invalid current item. it's value: " + ix);
        return null;
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

    public interface PageChangedListener{
        void onPageChanged(Page page);
    }

}
