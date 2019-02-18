package com.example.trafimau_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentViewHolder> {

    private final DataModel dataModel;
    final String LOG_TAG = "listAdapter";

    ListFragmentAdapter(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @NonNull
    @Override
    public ListFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_fragment_app_view, viewGroup, false);
        return new ListFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFragmentViewHolder viewHolder, int i) {
        // TODO: check null collor
        Integer color = dataModel.getColor(i);
        if(color == null){
            Log.d(LOG_TAG, "color at position " + i + " is null");
            return;
        }
        viewHolder.bind(color, "Sample string");
    }

    @Override
    public int getItemCount() {
        return dataModel.getItemCount();
    }
}
