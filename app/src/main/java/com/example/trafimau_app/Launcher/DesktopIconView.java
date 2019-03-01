package com.example.trafimau_app.Launcher;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class DesktopIconView extends AppCompatImageView {

    public DesktopIconView(Context context) {
        super(context);
    }

    public DesktopIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DesktopIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
