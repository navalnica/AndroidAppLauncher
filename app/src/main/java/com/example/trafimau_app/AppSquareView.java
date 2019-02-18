package com.example.trafimau_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class AppSquareView extends View {

    public AppSquareView(Context context) {
        super(context);
    }

    public AppSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppSquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}