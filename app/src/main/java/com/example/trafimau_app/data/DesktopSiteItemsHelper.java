package com.example.trafimau_app.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.view.View;

import com.example.trafimau_app.MyApplication;
import com.example.trafimau_app.data.db.DesktopSiteItem;
import com.example.trafimau_app.data.db.DesktopSiteItemDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DesktopSiteItemsHelper {

    private MyApplication app;
    private DesktopSiteItemDao desktopSiteItemDao;
    public final HashMap<Integer, DesktopSiteItemWithIcon> sites = new HashMap<>();

    public DesktopSiteItemsHelper(MyApplication app) {
        this.app = app;
        desktopSiteItemDao = this.app.db.desktopSiteItemDao();
        loadDataFromDB();
    }

    public void addItem(DesktopSiteItemWithIcon item, View itemView) {
        sites.put(item.index, item);
        desktopSiteItemDao.insert(item);
    }

    public DesktopSiteItemWithIcon getItemByIndex(int index) {
        return sites.get(index);
    }

    @WorkerThread
    private Bitmap getSiteIconFromCacheFile(DesktopSiteItem item) {
        final Bitmap bitmap = BitmapFactory.decodeFile(item.pathToIconCache);
        Log.d(MyApplication.LOG_TAG,
                "DesktopSiteItemsHelper. icon cache for " + item.shortLink +
                " is null: " + (bitmap == null));
        return bitmap;
    }

    private void loadDataFromDB() {
        new Thread(() -> {
            List<DesktopSiteItem> desktopItems = desktopSiteItemDao.getAllInList();
            for (DesktopSiteItem item : desktopItems) {
                DesktopSiteItemWithIcon itemWithIcon = new DesktopSiteItemWithIcon(item);
                Bitmap cached = getSiteIconFromCacheFile(item);
                if (cached == null) {
                    querySiteIcon(itemWithIcon);
                }
                else{
                    itemWithIcon.icon = cached;
                }
                sites.put(itemWithIcon.index, itemWithIcon);
            }
        }).run();
    }

    private void querySiteIcon(DesktopSiteItemWithIcon item) {
        String requestUrl = "https://favicon.yandex.net/favicon/" + item.shortLink + "?size=120";
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        Log.d(MyApplication.LOG_TAG, "fetching site icon for " + item.shortLink);

        OkHttpClient client = new OkHttpClient();
        // todo: check internet connection
        client.newCall(request).enqueue(new LoadSiteIconCallback(
                item, app.desktopIconDimensionInPx, this));
    }

    public void storeSiteIconToCache(DesktopSiteItemWithIcon item) {
        new Thread(() -> {
            File cacheFile = new File(app.getCacheDir(), item.shortLink + ".png");
            try (FileOutputStream out = new FileOutputStream(cacheFile)) {
                item.icon.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                Log.e(MyApplication.LOG_TAG,
                        "exception in DesktopSiteItemsHelper.storeSiteIconToCache: " + e.getMessage());
            }
            item.pathToIconCache = cacheFile.getAbsolutePath();
            desktopSiteItemDao.updatePathToIconCacheFile(item.index, item.pathToIconCache);
            Log.d(MyApplication.LOG_TAG, "stored site icon to " + item.pathToIconCache);
        }).run();
    }
}
