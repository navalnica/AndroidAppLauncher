package com.example.trafimau_app;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ListActivityAppAdapter extends RecyclerView.Adapter<ListActivityAppViewHolder> {

    // TODO: move data creation in separate model
    private final Map<Integer, Integer> colorMap = new HashMap<>();
    private final Random random = new Random();

    @NonNull
    @Override
    public ListActivityAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_activity_app_view, viewGroup, false);
        return new ListActivityAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListActivityAppViewHolder viewHolder, int i) {
        viewHolder.bind(getColor(i), "Sample string");
    }

    @Override
    public int getItemCount() {
        // TODO: set common number of items for Launcher and for List activities
        return 200;
    }

    private int getColor(int i) {
        Integer color = colorMap.get(i);
        if (color == null) {
            color = Color.rgb(
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
            );
            colorMap.put(i, color);
        }
        return color;
    }
}
