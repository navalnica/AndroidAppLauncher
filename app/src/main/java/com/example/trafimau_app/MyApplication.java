package com.example.trafimau_app;

import android.app.Application;

public class MyApplication extends Application {

    public final static String LOG_TAG = "MyApp";

    public enum Theme {
        LIGHT, DARK
    }

    public enum Layout {
        STANDARD(4, 6),
        COMPACT(5, 7);

        public final int portraitGridSpanCount;
        public final int landscapeGridSpanCount;

        Layout(int portraitGridSpanCount, int landscapeGridSpanCount) {
            this.portraitGridSpanCount = portraitGridSpanCount;
            this.landscapeGridSpanCount = landscapeGridSpanCount;
        }
    }

    public Theme theme = Theme.LIGHT;
    public Layout layout = Layout.STANDARD;
    public DataModel dataModel = new DataModel();

}
