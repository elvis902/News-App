package com.example.newsappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{


    private static int LOADER_ID = 0;
    public static final String LOG_TAG = MainActivity.class.getName();

//    SwipeRefreshLayout swipe;
    public static final String API_KEY = "17c85ad5-8d65-4f3c-b613-1a956bf0be05";
    public static final String REQUEST_URL = "https://content.guardianapis.com/search?q=debates";
   public static final String TEMPO_URL = "https://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2014-01-01&api-key=test";
    private NewsAdapter adapter;
    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;
    ListView newsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsListView = (ListView)findViewById(R.id.list);
        mEmptyStateTextView = (TextView)findViewById(R.id.empty_view);

        //Setting the Empty View
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        adapter = new NewsAdapter(this,new ArrayList<News>());
        newsListView.setAdapter(adapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News currentNews = adapter.getItem(position);
                assert currentNews != null;
                Uri newsUri = Uri.parse(currentNews.getUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW,newsUri);
                    startActivity(websiteIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo  networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected())
        {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getSupportLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);

        }else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }



    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        //return the url string
        return new  NewsLoader(this,TEMPO_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {


        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);
        // Clear the adapter of previous earthquake data
        adapter.clear();
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
            adapter.clear();
    }
}
