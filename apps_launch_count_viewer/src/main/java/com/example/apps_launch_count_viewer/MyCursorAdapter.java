package com.example.apps_launch_count_viewer;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCursorAdapter extends SimpleCursorAdapter {
    public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void setViewText(TextView v, String text) {
        if(v.getId() == R.id.last_launched){
            long timestamp = Long.parseLong(text);
            Date date = new Date(timestamp);
            text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            v.setText(text);
        }
        else{
            super.setViewText(v, text);
        }
    }
}
