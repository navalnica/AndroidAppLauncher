package com.example.trafimau_app.Launcher;

import android.content.Context;
import android.util.AttributeSet;

public class GridAppIconView extends android.support.v7.widget.AppCompatImageView {

    public GridAppIconView(Context context) {
        super(context);
    }

    public GridAppIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridAppIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}