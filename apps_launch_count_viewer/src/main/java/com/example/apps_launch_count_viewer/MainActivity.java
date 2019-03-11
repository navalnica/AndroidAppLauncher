package com.example.apps_launch_count_viewer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = "MyApp";
    private ContentResolver cr;
    private final Uri uriLaunched = Uri.parse("content://com.example.trafimau_app.provider/database");
    private final Uri uriLast = Uri.parse("content://com.example.trafimau_app.provider/database/last");
    private SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cr = getContentResolver();

        String[] columns = {"label", "package_name", "launched_count", "last_launched"};
        int[] viewItems = {R.id.label, R.id.packageName, R.id.launched_count, R.id.last_launched};
        adapter = new MyCursorAdapter(
                this, R.layout.list_item, null, columns, viewItems, 0);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        findViewById(R.id.loadLaunchedApps).setOnClickListener(v -> queryLaunchedApps());
        findViewById(R.id.loadLastApp).setOnClickListener(v -> queryLastLaunchedApp());
    }

    private void queryLaunchedApps() {
        try{
            Cursor cursor = cr.query(uriLaunched, null, null, null, null);
            if (cursor == null) {
                final String msg = "cursor is null";
                Log.e(LOG_TAG, msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            if(cursor.getCount() < 1){
                Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
            }
            Log.d(LOG_TAG, "all launched apps:");
            while (cursor.moveToNext()) {
                String label = cursor.getString(cursor.getColumnIndex("label"));
                int launchedCnt = cursor.getInt(cursor.getColumnIndex("launched_count"));
                Log.d(LOG_TAG, "label: " + label + "; cnt: " + launchedCnt);
            }
            adapter.changeCursor(cursor);
        } catch(SecurityException e){
            final String msg = "No READ permission granted for ContentProvider";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, msg);
        }
    }

    private void queryLastLaunchedApp(){
        try{
            Cursor cursor = cr.query(uriLast, null, null, null, null);
            if (cursor == null) {
                Log.e(LOG_TAG, "cursor is null");
                return;
            }
            Log.d(LOG_TAG, "last launched app:");
            if(cursor.moveToNext()){
                String label = cursor.getString(cursor.getColumnIndex("label"));
                int launchedCnt = cursor.getInt(cursor.getColumnIndex("launched_count"));
                Log.d(LOG_TAG, "label: " + label + "; cnt: " + launchedCnt);
            }
            else{
                Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
            }
            adapter.changeCursor(cursor);
        }
        catch(SecurityException e){
            final String msg = "No READ permission granted for ContentProvider";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, msg);
        }
    }
}
