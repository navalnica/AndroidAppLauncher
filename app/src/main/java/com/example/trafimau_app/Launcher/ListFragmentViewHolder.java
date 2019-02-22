package com.example.trafimau_app.Launcher;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.trafimau_app.MyAppInfo;
import com.example.trafimau_app.R;

public class ListFragmentViewHolder extends RecyclerView.ViewHolder {

    private AppCompatImageView appIconView;
    private TextView appLabelView;

    public ListFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
        appIconView = itemView.findViewById(R.id.listFragmentAppIcon);
        appLabelView = itemView.findViewById(R.id.listFragmentAppLabel);
    }

    public void bind(MyAppInfo appInfo) {
        appIconView.setBackgroundDrawable(appInfo.icon);
        appLabelView.setText(appInfo.label);
    }
}
