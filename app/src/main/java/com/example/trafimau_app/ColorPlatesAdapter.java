package com.example.trafimau_app;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class ColorPlatesAdapter extends RecyclerView.Adapter<ColorPlateViewHolder> {

    private final Map<Integer, Integer> colorMap = new HashMap<>();
    private final Random random = new Random();

    @NonNull
    @Override
    public ColorPlateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_color_plate, viewGroup, false);
        return new ColorPlateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPlateViewHolder colorPlateViewHolder, int i) {

        colorPlateViewHolder.bind(getColor(i));

        // display color plate color
        colorPlateViewHolder.itemView.setOnLongClickListener(v -> {
            final String tag = "PLATES";
            Integer color = colorMap.get(i);
            if(color == null){
                Log.d(tag, "plate color == null");
            }
            else{
                int rgb = color & 0xffffff;
                final String colorString = colorPlateViewHolder.itemView.
                        getContext().getString(R.string.color);
                Toast.makeText(colorPlateViewHolder.itemView.getContext(),
                        colorString + ": #" + Integer.toHexString(rgb), Toast.LENGTH_LONG)
                        .show();
            }

            return true;
        });

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

    @Override
    public int getItemCount() {
        return 200;
    }



}
