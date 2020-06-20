package com.codepath.apps.restclienttemplate;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.SampleModelDao;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListener {


    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 20;
    public final static int MAX_TWEET_LENGTH = 280;


    TwitterClient client;
    RecyclerView tweetsRV;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    EndlessRecyclerViewScrollListener endlessScroller;
    TweetDao tweetDao;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            //open up compose screen
//            Intent intent = new Intent(this, ComposeActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment editNameDialogFragment = ComposeFragment.newInstance("Some Title", false);
            editNameDialogFragment.show(fm, "fragment_edit_name");
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //get data from Tweet object and insert into recylerview
            Parcelable tweet = data.getParcelableExtra("tweet");
            Tweet mTweet = (Tweet) Parcels.unwrap(tweet);
            tweets.add(0, mTweet);
            adapter.notifyDataSetChanged();
            tweetsRV.smoothScrollToPosition(0);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("");
        actionbar.setLogo(R.drawable.ic_launcher_twitter);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        if (actionbar != null) {
//            actionbar.setDisplayUseLogoEnabled(true);
            actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter_blue)));
//            actionbar.setIcon(R.drawable.ic_launcher);
//            actionbar.setLogo(R.drawable.ic_launcher);
//            actionbar.show();
        }
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
        LinearLayoutManager llm = new LinearLayoutManager(this);
        tweetsRV.setLayoutManager(llm);
        endlessScroller = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "LOADMORE");
                loadNextDataFromApi(page);


            }
        };
        tweetsRV.setAdapter(adapter);
        tweetsRV.addOnScrollListener(endlessScroller);


        client = TwitterApp.getRestClient(this);

        //query for existing tweets in DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);

            }
        });

        populateHomeTimeline();

    }

    private void loadNextDataFromApi(int page) {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Loaded next page succesfully");
                try {
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Loaded next page FAILED", throwable);

            }
        }, tweets.get(tweets.size() - 1).id);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Populate timeline json request successful" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    adapter.clear();
                    adapter.addAll(tweetsFromNetwork);
                    swipeRefresh.setRefreshing(false);

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));


                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Parsing Tweets failed", e);
                    swipeRefresh.setRefreshing(false);

                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Populate timeline json request FAILED", throwable);
                swipeRefresh.setRefreshing(false);


            }
        });
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
//        Parcelable tweet = getIntent().getParcelableExtra("tweet");
//        Tweet mTweet = (Tweet) Parcels.unwrap(tweet);
        String tweetContent = inputText;
        if (tweetContent.isEmpty()) {
            Toast.makeText(this, "Please type a tweet", Toast.LENGTH_LONG).show();
//                return;
        }
        if (tweetContent.length() > MAX_TWEET_LENGTH) {
            Toast.makeText(this, "Tweet is too long, must be under 280 characters", Toast.LENGTH_LONG).show();
//                return;
        }
        Toast.makeText(this, tweetContent, Toast.LENGTH_LONG).show();
        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Tweet successfully published");
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    Log.i(TAG, "Tweet parsed: " + tweet.toString());
                    Intent intent = new Intent();
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, intent);
                    onActivityResult(REQUEST_CODE, RESULT_OK, intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to publish" + throwable.toString());
            }
        });

    }
}