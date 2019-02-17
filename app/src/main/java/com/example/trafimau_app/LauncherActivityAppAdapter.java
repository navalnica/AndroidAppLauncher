package com.example.trafimau_app;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LauncherActivityAppAdapter extends RecyclerView.Adapter<LauncherActivityAppViewHolder> {

    // TODO: move data creation in separate model
    private final Map<Integer, Integer> colorMap = new HashMap<>();
    private final Random random = new Random();

    @NonNull
    @Override
    public LauncherActivityAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.launcher_activity_app_view, viewGroup, false);
        return new LauncherActivityAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LauncherActivityAppViewHolder launcherActivityAppViewHolder, int i) {
        launcherActivityAppViewHolder.bind(getColor(i));

        // TODO: remove listener
        launcherActivityAppViewHolder.itemView.setOnLongClickListener(v -> {
            final String tag = "PLATES";
            Integer color = colorMap.get(i);
            if(color == null){
                Log.d(tag, "plate color == null");
            }
            else{
                int rgb = color & 0xffffff;
                final String colorString = launcherActivityAppViewHolder.itemView.
                        getContext().getString(R.string.color);
                Toast.makeText(launcherActivityAppViewHolder.itemView.getContext(),
                        colorString + ": #" + Integer.toHexString(rgb), Toast.LENGTH_LONG)
                        .show();
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return 1_000;
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
