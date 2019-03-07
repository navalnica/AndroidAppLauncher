package com.example.trafimau_app.Launcher;

import android.content.Context;
import android.util.AttributeSet;

public class SquareBasedOnWidthImageView extends android.support.v7.widget.AppCompatImageView {

    public SquareBasedOnWidthImageView(Context context) {
        super(context);
    }

    public SquareBasedOnWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareBasedOnWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}