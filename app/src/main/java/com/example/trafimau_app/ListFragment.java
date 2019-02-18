package com.example.trafimau_app;

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

public class ListFragment extends Fragment {

    private MyApplication app;
    private ListFragmentAdapter listFragmentAdapter;
    private LinearLayoutManager layoutManager;
    private View rootView;

    final String TAG = "ListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO: maybe remove super invocation?
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            app = (MyApplication) getActivity().getApplication();
            listFragmentAdapter = new ListFragmentAdapter(app.dataModel);
        }
        else{
            Log.d(TAG, "getActivity() returned null");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        configRecyclerView();
        return rootView;
    }

    private void configRecyclerView() {
        RecyclerView rv = rootView.findViewById(R.id.listFragmentRecyclerView);
        rv.setAdapter(listFragmentAdapter);
        layoutManager = new LinearLayoutManager(rv.getContext());
        rv.setLayoutManager(layoutManager);
    }
}
