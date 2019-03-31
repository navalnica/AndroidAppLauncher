package com.example.trafimau_app.activity.launcher;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.trafimau_app.data.DesktopSiteItemWithIcon;
import com.example.trafimau_app.data.LoadSiteIconCallback;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FragmentDesktop extends Fragment implements
        EnterSiteLinkDialog.EnterSiteLinkDialogListener {

    private MyApplication app;
    private ActivityLauncher activity;
    private LinearLayout table;

    private int rowsCount;
    private int columnsCount;

    private int clickedItemIx;
    private final String clickedItemIxKey = "clicked_item_ix";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ActivityLauncher) context;
        if (activity != null) {
            app = (MyApplication) activity.getApplication();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        if (savedInstanceState != null) {
            clickedItemIx = savedInstanceState.getInt(clickedItemIxKey, -1);
        }
        initDesktopItems();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(MyApplication.LOG_TAG, "FragmentDesktop.onSaveInstanceState");
        outState.putInt(clickedItemIxKey, clickedItemIx);
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

        if (app.getDesktopIconDimensionInPx() == 0) {
            View view = firstRow.getChildAt(0);
            FrameLayout frameLayout = view.findViewById(R.id.desktopItemFrameLayout);
            ViewTreeObserver vto = frameLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(() -> {
                if (app.getDesktopIconDimensionInPx() > 0) {
                    return;
                }
                int height = view.getHeight();
                int width = view.getWidth();
                // getHeight returns max height of FrameLayout
                // reduce it to free some space
                app.setDesktopIconDimensionInPx(Math.min(height, width) * 3 / 7);
                // load saved sites after icon dimension measured
                loadFromMemory();
            });
        } else {
            loadFromMemory();
        }
    }

    private View getItemViewByIndex(int ix) {
        int rowIx = ix / columnsCount;
        int colIx = ix % columnsCount;
        LinearLayout row = (LinearLayout) table.getChildAt(rowIx);
        final String msg = "passed invalid item ix: " + ix +
                "; rowsCount: " + rowsCount + "; columnsCount: " + columnsCount;
        if (row == null) {
            throw new NullPointerException(msg);
        }
        final View item = row.getChildAt(colIx);
        if (item == null) {
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

    private void loadFromMemory() {
        for (Map.Entry<Integer, DesktopSiteItemWithIcon> entry
                : app.siteItemsHelper.sites.entrySet()) {
            Integer ix = entry.getKey();
            DesktopSiteItemWithIcon item = entry.getValue();
            if (ix == null || item == null) {
                throw new NullPointerException(
                        "FragmentDesktop.loadFromMemory. found null entry in map");
            }

            View itemView = getItemViewByIndex(ix);
            TextView tv = itemView.findViewById(R.id.desktopItemTextView);
            tv.setText(item.shortLink);
            ImageView iconView = itemView.findViewById(R.id.desktopItemIconView);
            iconView.setImageBitmap(item.icon);
            itemView.setOnClickListener(this::openLinkInBrowser);
            registerForContextMenu(itemView);
            itemView.setTag(item.index);
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

        final DesktopSiteItemWithIcon newItem = new DesktopSiteItemWithIcon(clickedItemIx, URL);
        app.siteItemsHelper.addItem(newItem);

        clickedItemView.setOnClickListener(this::openLinkInBrowser);
        registerForContextMenu(clickedItemView);
        clickedItemView.setTag(newItem.index);

        TextView tv = clickedItemView.findViewById(R.id.desktopItemTextView);
        AppCompatImageView iconView = clickedItemView.findViewById(R.id.desktopItemIconView);
        ProgressBar progressBar = clickedItemView.findViewById(R.id.desktopItemProgressBar);

        tv.setText(newItem.shortLink);
        // show progress bar until request resolves
        iconView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String requestUrl = "https://favicon.yandex.net/favicon/" + newItem.shortLink + "?size=120";
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        Log.d(MyApplication.LOG_TAG, "fetching site icon for " + newItem.shortLink);
        OkHttpClient client = new OkHttpClient();
        // todo: check internet connection
        client.newCall(request).enqueue(new LoadSiteIconCallback(
                newItem, app.getDesktopIconDimensionInPx(), app.siteItemsHelper, activity,
                () -> {
                    iconView.setImageBitmap(newItem.icon);
                    progressBar.setVisibility(View.INVISIBLE);
                    iconView.setVisibility(View.VISIBLE);
                },
                () -> iconView.setBackground(getResources().getDrawable(R.drawable.ic_warning_black_24dp))
        ));
    }

    private void openLinkInBrowser(View view) {
        int itemIx = getItemIndexByView(view);
        DesktopSiteItemWithIcon item = app.siteItemsHelper.getItemByIndex(itemIx);
        if (item == null || item.shortLink == null) {
            throw new NullPointerException(
                    "FragmentDesktop.openLinkInBrowser: item or it's shortLink is null");
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(item.uri);
        startActivity(i);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        activity.onCreateContextMenuForSite(menu, v, menuInfo, this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return activity.onSiteContextItemSelected(item);
    }

    public void deleteSiteItemByIndex(int index){
        View itemView = getItemViewByIndex(index);
        itemView.setOnClickListener(this::querySiteLink);
        unregisterForContextMenu(itemView);

        TextView tv = itemView.findViewById(R.id.desktopItemTextView);
        ImageView iv = itemView.findViewById(R.id.desktopItemIconView);
        tv.setText("");
        iv.setImageBitmap(null);

        app.siteItemsHelper.deleteItem(index);
    }
}
