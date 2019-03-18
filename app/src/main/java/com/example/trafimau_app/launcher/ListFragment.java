package com.example.trafimau_app.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;

public class ListFragment extends Fragment {

    private MyApplication app;
    private LauncherAppAdapter launcherAppAdapter;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            app = (MyApplication) getActivity().getApplication();
            launcherAppAdapter = new LauncherAppAdapter(app, R.layout.list_app_item);
        }
        else{
            Log.d(MyApplication.LOG_TAG, "getActivity() returned null");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_launcher_list, container, false);
        configRecyclerView();
        return rootView;
    }

    private void configRecyclerView() {
        RecyclerView rv = rootView.findViewById(R.id.listFragmentRecyclerView);
        rv.setAdapter(launcherAppAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rv.getContext());
        rv.setLayoutManager(layoutManager);
    }
}
