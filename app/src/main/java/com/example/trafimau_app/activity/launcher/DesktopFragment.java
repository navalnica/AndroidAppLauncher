package com.example.trafimau_app.activity.launcher;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class DesktopFragment extends Fragment implements
        EnterSiteLinkDialog.EnterSiteLinkDialogListener {

    private MyApplication app;
    private TableLayout tableLayout;

    // correspond to TableLayout in fragment_desktop.xml
    private int rowsCount;
    private int columnsCount;

    private View clickedItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            app = (MyApplication) activity.getApplication();
        }

        Log.d(MyApplication.LOG_TAG, "DesktopFragment.onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(MyApplication.LOG_TAG, "DesktopFragment.onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_desktop, container, false);

        tableLayout = rootView.findViewById(R.id.desktopTable);
        addListenersForTableItems(tableLayout);

        loadSavedSites();

        Log.d(MyApplication.LOG_TAG, "DesktopFragment.onCreateView");

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(MyApplication.LOG_TAG, "DesktopFragment.onSaveInstanceState");
    }

    int getTableItemIndexByView(View v) {
        TableRow parentRow = (TableRow) v.getParent();
        TableLayout parentTable = (TableLayout) parentRow.getParent();
        int rowIx = parentTable.indexOfChild(parentRow);
        int colIx = parentRow.indexOfChild(v);
        return rowIx * columnsCount + colIx;
    }

    private void loadSavedSites() {
        // TODO: store data in DB
        for (Map.Entry<Integer, SiteInfo> entry : app.sitesDataModel.sites.entrySet()) {
            Integer ix = entry.getKey();
            SiteInfo info = entry.getValue();
            if (ix == null || info == null) {
                YandexMetrica.reportEvent(
                        "DesktopFragment.loadSavedSites. found null entry in map");
                continue;
            }
            int rowIx = ix / columnsCount;
            int colIx = ix % columnsCount;

            Log.d(MyApplication.LOG_TAG,
                    "Restoring site info for item with ix: " + ix
                            + " row: " + rowIx + " colIx: " + colIx);
            YandexMetrica.reportEvent("DesktopFragment: loading saved items");


            TableRow row = (TableRow) tableLayout.getChildAt(rowIx);
            View item = row.getChildAt(colIx);

            TextView tv = item.findViewById(R.id.desktopItemTextView);
            tv.setText(info.link);

            ImageView iconView = item.findViewById(R.id.desktopItemIconView);
            // TODO: load actual stored icon
            iconView.setBackground(getResources().getDrawable(R.drawable.ic_warning_black_80dp));

            item.setOnClickListener(this::openLinkInBrowser);
        }
    }

    private void addListenersForTableItems(TableLayout tableLayout) {
        rowsCount = tableLayout.getChildCount();
        TableRow firstRow = (TableRow) tableLayout.getChildAt(0);
        columnsCount = firstRow.getChildCount();

        for (int i = 0; i < rowsCount; ++i) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < columnsCount; ++j) {
                row.getChildAt(j).setOnClickListener(this::setSiteInfoForDesktopItem);
            }
        }
    }

    private void setSiteInfoForDesktopItem(View v) {
        clickedItem = v;
        EnterSiteLinkDialog dialog = new EnterSiteLinkDialog();
        dialog.setTargetFragment(this, 0);

        // the top level fragment is AppsFragment.
        // it uses ViewPager and loads other fragments
        // including DesktopFragment with the use of
        // getChildFragmentManager().
        // so getFragmentManager() here will result in parents getChildFragmentManager()
        FragmentManager curFragmentManager = getFragmentManager();

        if (curFragmentManager == null) {
            final String msg = "DesktopFragment: getFragmentManager() == null";
            Log.d(MyApplication.LOG_TAG, msg);
            YandexMetrica.reportEvent(msg);
            throw new NullPointerException(msg);
        }

        dialog.show(getFragmentManager(), "EnterSiteLinkDialog");

        YandexMetrica.reportEvent("DesktopFragment: loading site info for desktop item");
    }

    @Override
    public void onLinkSetFromDialog(String URL) {
        int itemIx = getTableItemIndexByView(clickedItem);

        final String msg = "Desktop item clicked! ix: " + itemIx;
        Log.d(MyApplication.LOG_TAG, msg);
        YandexMetrica.reportEvent(msg);

        // save site sites
        app.sitesDataModel.putSiteLink(itemIx, URL);
        // set new OnClickListener
        clickedItem.setOnClickListener(this::openLinkInBrowser);

        TextView tv = clickedItem.findViewById(R.id.desktopItemTextView);
        AppCompatImageView iconView = clickedItem.findViewById(R.id.desktopItemIconView);
        ProgressBar progressBar = clickedItem.findViewById(R.id.desktopItemProgressBar);

        // show progress bar until request resolves
        iconView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        tv.setText(URL);
        String requestUrl = "https://favicon.yandex.net/favicon/" + URL + "?size=120";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(
                new Callback() {

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        final String msg = "site icon request for " + URL
                                + " failed with exception: " + e.getMessage();
                        Log.d(MyApplication.LOG_TAG, msg);
                        YandexMetrica.reportEvent(msg);
                        getActivity().runOnUiThread(() -> {
                            iconView.setBackground(getResources().getDrawable(R.drawable.ic_warning_black_80dp));
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response)
                            throws IOException {
                        if (!response.isSuccessful()) {
                            final String msg = "site icon request for " + URL
                                    + " resulted in unexpected code: " + response;
                            YandexMetrica.reportEvent(msg);
                            throw new IOException(msg);
                        }

                        // TODO: check if site exists / is reachable.
                        // if not - show warning icon

                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            byte[] bitmapBytes = responseBody.bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,
                                    0, bitmapBytes.length);
                            // blank desktop item has width = 80dp
                            int iconWidthInDp = pxToDp(80);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                                    iconWidthInDp, iconWidthInDp, false);

                            getActivity().runOnUiThread(() -> {
                                Drawable drawable = new BitmapDrawable(getResources(), scaledBitmap);
                                iconView.setBackground(drawable);
                                progressBar.setVisibility(View.INVISIBLE);
                                iconView.setVisibility(View.VISIBLE);
                            });

                        } else {
                            final String msg = "response for " + URL + " has null body";
                            Log.d(MyApplication.LOG_TAG, msg);
                            YandexMetrica.reportEvent(msg);
                        }

                    }


                });
    }

    int pxToDp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, getResources().getDisplayMetrics());
    }

    private void openLinkInBrowser(View view) {
        int itemIx = getTableItemIndexByView(view);
        String link = app.sitesDataModel.getLink(itemIx);
        if (link == null) {
            final String msg = "DesktopFragment.openLinkInBrowser: link == null";
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

//    private TableLayout inflateTable() {

//        // try to infalte table dynamically
//
//        TableLayout tableLayout = new TableLayout(getContext());
//        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
//                TableLayout.LayoutParams.MATCH_PARENT,
//                TableLayout.LayoutParams.MATCH_PARENT
//        ));
//        tableLayout.setStretchAllColumns(true);
//        tableLayout.setWeightSum(rowsCount);
//
//        for (int i = 0; i < rowsCount; ++i) {
//            TableRow row = new TableRow(getContext());
//            row.setLayoutParams(new TableRow.LayoutParams(
//                    TableRow.LayoutParams.MATCH_PARENT,
//                    0,
//                    1.0f
//            ));
//
//            for (int j = 0; j < columnsCount; ++j) {
//                View v = getLayoutInflater().inflate(R.layout.desktop_table_item,
//                        row, false);
//
//                TextView tv = (TextView) v;
//                if (i % 2 == 0) {
//                    tv.setText(getString(R.string.layoutCompactDescription));
//                }
//
//                row.addView(v);
//            }
//
//            tableLayout.addView(row);
//        }
//        return tableLayout;
//    }

}
