package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {


    public static String TWEET_EXTRA = "TWEET_EXTRA";
    public static String TAG = "TweetDetailActivity";


    ImageView profImage;
    TextView handle;
    TextView description;
    ImageView shareBtn;
    ImageView retweetBtn;
    ImageView mediaImg;
    TextView retweetsTV;
    TextView favsTV;
    Button replyBtn;
    EditText replyET;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        final Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(TWEET_EXTRA));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("");
        actionbar.setLogo(R.drawable.ic_launcher_twitter);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        if (actionbar != null) {
//            actionbar.setDisplayUseLogoEnabled(true);
            actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter_blue)));
//            actionbar.setIcon(R.drawable.ic_launcher);
//            actionbar.setLogo(R.drawable.ic_launcher);
//            actionbar.show();
        }
//        Log.i(TAG, tweet.user.imageURL);
        profImage = findViewById(R.id.profilePicIV);
        mediaImg = findViewById(R.id.mediaIV);
        description = findViewById(R.id.descriptionTV);
        handle = findViewById(R.id.handleTV);
        shareBtn = findViewById(R.id.shareButton);
        replyBtn = findViewById(R.id.replyButton);
        replyET = findViewById(R.id.replyET);
        retweetBtn = findViewById(R.id.retweetButton);
        retweetsTV = findViewById(R.id.retweetsTV);
        favsTV = findViewById(R.id.favsTV);
        Glide.with(this).load(tweet.user.imageURL).transform(new RoundedCornersTransformation(30, 5)).into(profImage);
        if (tweet.mediaURL.isEmpty()) {
            mediaImg.setVisibility(View.GONE);
        } else {
            double width = (mediaImg.getWidth() * 0.9);
            int iwidth = ((int) width);
            Glide.with(this).load(tweet.mediaURL).override(iwidth, 150).transform(new RoundedCornersTransformation(50, 5)).into(mediaImg);
        }
        client = new TwitterClient(this);
        favsTV.setText(String.valueOf(tweet.favCount));
        retweetsTV.setText(String.valueOf(tweet.retweetCount));
        handle.setText(String.valueOf(tweet.user.screenName));
        description.setText(String.valueOf(tweet.body));
        replyET.setText("@" + tweet.user.screenName);

        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bodyLength = replyET.getText().length();
                if (bodyLength > 0 && bodyLength <= 280) {
                    Log.i(TAG, replyET.getText().toString());

                    client.publishTweetInReplyTo(replyET.getText().toString(), tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "TWEET reply sent succesfully");
                            Toast.makeText(TweetDetailActivity.this, "Tweet sent successfully!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Toast.makeText(TweetDetailActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                            Log.i(TAG, "ERROR" + throwable.toString());


                        }
                    });
                }
            }
        });


    }
}