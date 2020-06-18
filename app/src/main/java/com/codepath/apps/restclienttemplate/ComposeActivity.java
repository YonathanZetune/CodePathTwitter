package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public final static int MAX_TWEET_LENGTH = 280;
    public final static String TAG = "ComposeActivity";


    EditText composeET;
    Button tweetButton;
    TwitterClient client;
    TextView charCountTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        composeET = findViewById(R.id.composeET);
        tweetButton = findViewById(R.id.buttonTweet);
        client = new TwitterClient(this);
        charCountTV = findViewById(R.id.charCountTV);
        charCountTV.setText(String.valueOf(MAX_TWEET_LENGTH));

        composeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "Text Changed On");
                int bodyLength = charSequence.length();
                int newCount = MAX_TWEET_LENGTH - bodyLength;
                charCountTV.setText(String.valueOf(newCount));
                if (newCount < 0) {
                    charCountTV.setTextColor(getResources().getColor(R.color.medium_red));
                } else if (newCount < 280 && newCount >= 0){
                    charCountTV.setTextColor(getResources().getColor(R.color.black));
                    tweetButton.setTextColor(getResources().getColor(R.color.white));

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = composeET.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Please type a tweet", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Tweet is too long, must be under 280 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
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
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Failed to publish" + throwable.toString());
                    }
                });
                //make api call to send tweet

            }
        });
    }
}