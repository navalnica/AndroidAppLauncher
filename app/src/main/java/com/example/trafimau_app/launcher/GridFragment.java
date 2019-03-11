package com.example.trafimau_app.launcher;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class GridFragment extends Fragment {

    private MyApplication app;
    private GridFragmentAdapter gridFragmentAdapter;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            app = (MyApplication) getActivity().getApplication();
            gridFragmentAdapter = new GridFragmentAdapter(app);
        }
        else{
            Log.d(MyApplication.LOG_TAG, "getActivity() returned null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_launcher_grid, container, false);
        configRecyclerView();
        return rootView;
    }

    private void configRecyclerView() {
        RecyclerView rv = rootView.findViewById(R.id.gridFragmentRecyclerView);
        rv.setAdapter(gridFragmentAdapter);
        int gridSpanCount = 1;
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            gridSpanCount = app.currentLayout.portraitGridSpanCount;
        } else {
            gridSpanCount = app.currentLayout.landscapeGridSpanCount;
        }
        LinearLayoutManager layoutManager = new GridLayoutManager(rv.getContext(), gridSpanCount);
        rv.setLayoutManager(layoutManager);

        int offset = getResources().getDimensionPixelOffset(R.dimen.smallOffset);
        rv.addItemDecoration(new GridFragmentDecorator(offset));
    }
}
