package com.example.trafimau_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SquareItemView extends View {

    public SquareItemView(Context context) {
        super(context);
    }

    public SquareItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
