package com.example.trafimau_app;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GridFragment extends Fragment {

    private MyApplication app;
    private GridFragmentAdapter gridFragmentAdapter;
    private LinearLayoutManager layoutManager;
    private View rootView;

    final String TAG = "GridFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            app = (MyApplication) getActivity().getApplication();
            gridFragmentAdapter = new GridFragmentAdapter(app.dataModel);
        }
        else{
            Log.d(TAG, "getActivity() returned null");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_grid, container, false);
        configRecyclerView();
        return rootView;
    }

    private void configRecyclerView() {
        RecyclerView rv = rootView.findViewById(R.id.gridFragmentRecyclerView);
        rv.setAdapter(gridFragmentAdapter);
        int gridSpanCount = 1;
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            gridSpanCount = app.layout.portraitGridSpanCount;
        } else {
            gridSpanCount = app.layout.landscapeGridSpanCount;
        }
        layoutManager = new GridLayoutManager(rv.getContext(), gridSpanCount);
        rv.setLayoutManager(layoutManager);

        int offset = getResources().getDimensionPixelOffset(R.dimen.recyclerViewOffset);
        rv.addItemDecoration(new GridFragmentDecorator(offset));
    }
}
