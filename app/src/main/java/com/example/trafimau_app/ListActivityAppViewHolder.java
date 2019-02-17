package com.example.trafimau_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListActivityAppViewHolder extends RecyclerView.ViewHolder {

    public ListActivityAppViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void bind(int color, String itemDescription){
        itemView.findViewById(R.id.listActivityAppIcon).setBackgroundColor(color);

        int rgb = color & 0xffffff;
        final String hexString = "#" + Integer.toHexString(rgb);
        TextView appName = itemView.findViewById(R.id.listActivityAppName);
        appName.setText(hexString);

        TextView appDescription = itemView.findViewById(R.id.listActivityAppDescription);
        appDescription.setText(itemDescription);
    }
}
