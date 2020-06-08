package com.example.newsappdemo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /** Tag for log messages */
    public static final String LOG_TAG = MainActivity.class.getName();

    /** Query URL */
    private String mUrl;




    public NewsLoader(@NonNull Context context, String url) {

        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
//        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
