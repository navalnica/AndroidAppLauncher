package com.example.trafimau_app.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.trafimau_app.MyApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoadSiteIconCallback implements Callback {

    public interface UICallable {
        void run();
    }

    private DesktopSiteItemWithIcon item;
    private int iconDimensionInPx;
    private DesktopSiteItemsHelper helper;
    private Activity activity;
    private UICallable onSuccessUIListener;
    private UICallable onFailureUIListener;

    public LoadSiteIconCallback(
            DesktopSiteItemWithIcon item,
            int iconDimensionInPx,
            DesktopSiteItemsHelper helper,
            Activity activity,
            UICallable onSuccessUIListener,
            UICallable onFailureUIListener) {
        this.item = item;
        this.iconDimensionInPx = iconDimensionInPx;
        this.activity = activity;
        this.helper = helper;
        this.onSuccessUIListener = onSuccessUIListener;
        this.onFailureUIListener = onFailureUIListener;
    }

    public LoadSiteIconCallback(
            DesktopSiteItemWithIcon item,
            int iconDimensionInPx,
            DesktopSiteItemsHelper helper) {
        this.item = item;
        this.iconDimensionInPx = iconDimensionInPx;
        this.helper = helper;
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        final String msg = "exception while icon fetching: " + e.getMessage();
        Log.e(MyApplication.LOG_TAG, msg);
        e.printStackTrace();

        if(activity != null && onFailureUIListener != null){
            activity.runOnUiThread(() -> onFailureUIListener.run());
        }
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull final Response response)
            throws IOException {
        if (!response.isSuccessful()) {
            final String msg = "site icon request for " + item.shortLink
                    + " resulted in unexpected code: " + response;
            throw new IOException(msg);
        }

        // TODO: check if site exists / is reachable.

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            byte[] bitmapBytes = responseBody.bytes();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,
                    0, bitmapBytes.length);
            item.icon = Bitmap.createScaledBitmap(
                    bitmap, iconDimensionInPx, iconDimensionInPx, false);
            helper.storeSiteIconToCache(item);

            Log.d(MyApplication.LOG_TAG, "site icon has been successfully loaded");

            if(activity != null && onSuccessUIListener != null){
                activity.runOnUiThread(() -> onSuccessUIListener.run());
            }
        } else {
            final String msg = "response for " + item.shortLink + " has null body";
            throw new NullPointerException(msg);
        }
    }
}