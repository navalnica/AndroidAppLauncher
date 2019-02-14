package com.example.trafimau_app;

import android.support.v7.widget.RecyclerView;
import android.view.View;

class ColorPlateViewHolder extends RecyclerView.ViewHolder {

    public ColorPlateViewHolder(View itemView){
        super(itemView);
    }

    public void bind(int color) {
        itemView.setBackgroundColor(color);
    }
}
