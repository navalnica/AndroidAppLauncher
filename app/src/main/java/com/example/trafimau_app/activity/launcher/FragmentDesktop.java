package com.example.trafimau_app.activity.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.R;
import com.example.trafimau_app.data.SiteInfo;
import com.yandex.metrica.YandexMetrica;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FragmentDesktop extends Fragment implements
        EnterSiteLinkDialog.EnterSiteLinkDialogListener {

    private MyApplication app;
    private Activity activity;
    private LinearLayout table;

    private int rowsCount;
    private int columnsCount;

    private int itemDimensionInPx;
    private final String itemDimensionInPxKey = "itemDimensionInPx_count";

    private int clickedItemIx;
    private final String clickedItemIxKey = "clicked_item_ix";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MyApplication.LOG_TAG, "FragmentDesktop.onCreate");

        activity = getActivity();
        if (activity != null) {
            app = (MyApplication) activity.getApplication();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(MyApplication.LOG_TAG, "FragmentDesktop.onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_desktop, container, false);
        table = rootView.findViewById(R.id.desktopTable);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(MyApplication.LOG_TAG, "FragmentDesktop.onActivityCreated. " +
                "savedInstanceState is null: " +
                (savedInstanceState == null));

        if(savedInstanceState != null){
            clickedItemIx = savedInstanceState.getInt(clickedItemIxKey, -1);
            itemDimensionInPx = savedInstanceState.getInt(itemDimensionInPxKey, 0);
        }
        initDesktopItems();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(MyApplication.LOG_TAG, "FragmentDesktop.onSaveInstanceState");
        outState.putInt(clickedItemIxKey, clickedItemIx);
        outState.putInt(itemDimensionInPxKey, itemDimensionInPx);
    }

    private void initDesktopItems() {
        rowsCount = table.getChildCount();
        LinearLayout firstRow = (LinearLayout) table.getChildAt(0);
        columnsCount = firstRow.getChildCount();

        for (int i = 0; i < rowsCount; ++i) {
            LinearLayout row = (LinearLayout) table.getChildAt(i);
            for (int j = 0; j < columnsCount; ++j) {
                row.getChildAt(j).setOnClickListener(this::querySiteLink);
            }
        }

        Log.d(MyApplication.LOG_TAG, "desktop item dimension in px: " + itemDimensionInPx);
        if(itemDimensionInPx == 0){
            View view = firstRow.getChildAt(0);
            FrameLayout frameLayout = view.findViewById(R.id.desktopItemFrameLayout);
            ViewTreeObserver vto = frameLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(() -> {
                if(itemDimensionInPx > 0) {
                    return;
                }
                int height = view.getHeight();
                int width = view.getWidth();
                // getHeight returns max height of FrameLayout
                // reduce it to free some space
                itemDimensionInPx = Math.min(height, width) * 5 / 9;
                // load saved sites after icon dimension measured
                loadSavedSites();
            });
        }
        else{
            loadSavedSites();
        }
    }

    private View getItemViewByIndex(int ix){
        int rowIx = ix / columnsCount;
        int colIx = ix % columnsCount;
        LinearLayout row = (LinearLayout) table.getChildAt(rowIx);
        final String msg = "passed invalid item ix: " + ix +
                "; rowsCount: " + rowsCount + "; columnsCount: " + columnsCount;
        if(row == null){
            throw new NullPointerException(msg);
        }
        final View item = row.getChildAt(colIx);
        if(item == null){
            throw new NullPointerException(msg);
        }
        return item;
    }

    int getItemIndexByView(View v) {
        LinearLayout parentRow = (LinearLayout) v.getParent();
        int rowIx = table.indexOfChild(parentRow);
        int colIx = parentRow.indexOfChild(v);
        return rowIx * columnsCount + colIx;
    }

    private void loadSavedSites() {
        // TODO: get data from DB
        for (Map.Entry<Integer, SiteInfo> entry : app.sitesDataModel.sites.entrySet()) {
            Integer ix = entry.getKey();
            SiteInfo info = entry.getValue();
            if (ix == null || info == null) {
                throw new NullPointerException(
                        "FragmentDesktop.loadSavedSites. found null entry in map");
            }

            View item = getItemViewByIndex(ix);
            TextView tv = item.findViewById(R.id.desktopItemTextView);
            tv.setText(info.link);

            ImageView iconView = item.findViewById(R.id.desktopItemIconView);
            // TODO: load actual stored icon
            iconView.setBackground(getResources().getDrawable(R.drawable.ic_warning_black_80dp));

            item.setOnClickListener(this::openLinkInBrowser);
        }
    }

    private void querySiteLink(View v) {
        clickedItemIx = getItemIndexByView(v);
        Log.d(MyApplication.LOG_TAG, "Desktop item clicked! ix: " + clickedItemIx);

        EnterSiteLinkDialog dialog = new EnterSiteLinkDialog();
        dialog.setTargetFragment(this, 0);

        // the top level fragment is FragmentLauncher.
        // it uses ViewPager and loads other fragments
        // including FragmentDesktop with the use of
        // getChildFragmentManager().
        // so getFragmentManager() here will result in parents getChildFragmentManager()
        FragmentManager curFragmentManager = getFragmentManager();
        if (curFragmentManager == null) {
            throw new NullPointerException("FragmentDesktop: getFragmentManager() == null");
        }
        dialog.show(getFragmentManager(), "EnterSiteLinkDialog");
    }

    @Override
    public void onSiteLinkReceived(String URL) {
        View clickedItemView = getItemViewByIndex(clickedItemIx);

        // todo: add to db
        app.sitesDataModel.putSiteLink(clickedItemIx, URL);

        clickedItemView.setOnClickListener(this::openLinkInBrowser);
        TextView tv = clickedItemView.findViewById(R.id.desktopItemTextView);
        AppCompatImageView iconView = clickedItemView.findViewById(R.id.desktopItemIconView);
        ProgressBar progressBar = clickedItemView.findViewById(R.id.desktopItemProgressBar);

        tv.setText(URL);
        // show progress bar until request resolves
        iconView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String requestUrl = "https://favicon.yandex.net/favicon/" + URL + "?size=120";
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        Log.d(MyApplication.LOG_TAG, "fetching site icon for " + URL);
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(
                new Callback() {

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        final String msg = "exception while icon fetching: " + e.getMessage();
                        Log.e(MyApplication.LOG_TAG, msg);
                        e.printStackTrace();
                        activity.runOnUiThread(() ->
                                iconView.setBackground(getResources().getDrawable(
                                        R.drawable.ic_warning_black_80dp)));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response)
                            throws IOException {
                        if (!response.isSuccessful()) {
                            final String msg = "site icon request for " + URL
                                    + " resulted in unexpected code: " + response;
                            throw new IOException(msg);
                        }

                        // TODO: check if site exists / is reachable.
                        // if not - show warning icon

                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            byte[] bitmapBytes = responseBody.bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,
                                    0, bitmapBytes.length);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                                    itemDimensionInPx, itemDimensionInPx, false);
                            Drawable drawable = new BitmapDrawable(getResources(), scaledBitmap);

                            activity.runOnUiThread(() -> {
                                iconView.setBackground(drawable);
                                progressBar.setVisibility(View.INVISIBLE);
                                iconView.setVisibility(View.VISIBLE);
                                Log.d(MyApplication.LOG_TAG, "Successfully set site icon");
                            });

                        } else {
                            final String msg = "response for " + URL + " has null body";
                            throw new NullPointerException(msg);
                        }
                    }
                });
    }

    int pxToDp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, getResources().getDisplayMetrics());
    }

    private void openLinkInBrowser(View view) {
        int itemIx = getItemIndexByView(view);
        String link = app.sitesDataModel.getLink(itemIx);
        if (link == null) {
            final String msg = "FragmentDesktop.openLinkInBrowser: link == null";
            Log.d(MyApplication.LOG_TAG, msg);
            YandexMetrica.reportEvent(msg);
            return;
        }
        if (!(link.startsWith("https://") || link.startsWith("http://"))) {
            link = "https://" + link;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(link);
        i.setData(uri);
        startActivity(i);
    }

}
