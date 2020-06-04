package com.example.newsappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    public static final int NEWS_LOADER_ID = 1;
    public static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search?q=debates&api-key=test";

    ListView mListView;
    TextView mTextView;
    NewsAdapter mAdapter;
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        mListView = (ListView) findViewById(R.id.list_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.text_view);

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager.getInstance(this).initLoader(NEWS_LOADER_ID,null,this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.no_internet_text);
        }



    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        mAdapter = new NewsAdapter(this, data);
        mProgressBar.setVisibility(View.GONE);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {

    }
}
