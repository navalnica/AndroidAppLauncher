package com.example.trafimau_app.Launcher;

import android.content.Context;
import android.util.AttributeSet;

public class ListAppIconView extends android.support.v7.widget.AppCompatImageView {


    public ListAppIconView(Context context) {
        super(context);
    }

    public ListAppIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListAppIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}