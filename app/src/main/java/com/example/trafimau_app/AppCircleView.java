package com.example.trafimau_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class AppCircleView extends View {
    public AppCircleView(Context context) {
        super(context);
    }

    public AppCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AppCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
