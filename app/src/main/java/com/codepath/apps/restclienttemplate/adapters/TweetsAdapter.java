package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.lang.annotation.Target;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tweetView = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Tweet tweet = tweets.get(position);
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profImage;
        TextView handle;
        TextView description;
        ImageView shareBtn;
        ImageView retweetBtn;
        ImageView mediaImg;
        TextView retweetsTV;
        TextView favsTV;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profImage = itemView.findViewById(R.id.profilePicIV);
            handle = itemView.findViewById(R.id.handleTV);
            description = itemView.findViewById(R.id.descriptionTV);
            shareBtn = itemView.findViewById(R.id.shareButton);
            retweetBtn = itemView.findViewById(R.id.retweetButton);
            mediaImg = itemView.findViewById(R.id.mediaIV);
            retweetsTV = itemView.findViewById(R.id.retweetsTV);
            favsTV = itemView.findViewById(R.id.favsTV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TweetDetailActivity.class);
                    context.startActivity(intent);

                }
            });

        }

        public void bind(Tweet tweet) {
            handle.setText(tweet.user.screenName);
            description.setText(tweet.body);
            retweetsTV.setText(String.valueOf(tweet.retweetCount));
            favsTV.setText(String.valueOf(tweet.favCount));
            double width = (mediaImg.getWidth() * 0.9);
            int iwidth = ((int) width);
            double height = (mediaImg.getHeight() * 0.9);
            int iheight = ((int) height);
            //programatically deteremine to show media image or not
            if (tweet.mediaURL.isEmpty()) {
                mediaImg.setVisibility(View.GONE);
            } else {
                Glide.with(context).load(tweet.mediaURL).override(iwidth, 150).transform(new RoundedCornersTransformation(50, 5)).into(mediaImg);

            }
            Glide.with(context).load(tweet.user.imageURL).transform(new RoundedCornersTransformation(30, 5)).into(profImage);
        }
    }


}
