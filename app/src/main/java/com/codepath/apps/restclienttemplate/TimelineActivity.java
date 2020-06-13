package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {


    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    RecyclerView tweetsRV;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "OnRefresh called");
                populateHomeTimeline();
            }
        });

        tweetsRV = findViewById(R.id.tweetsTimeline);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        tweetsRV.setLayoutManager(new LinearLayoutManager(this));
        tweetsRV.setAdapter(adapter);

        client = TwitterApp.getRestClient(this);
        populateHomeTimeline();

    }

    private void populateHomeTimeline() {
        client.getHomeTImeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Populate timeline json request successful" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Parsing Tweets failed", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Populate timeline json request FAILED", throwable);

            }
        });
    }
}