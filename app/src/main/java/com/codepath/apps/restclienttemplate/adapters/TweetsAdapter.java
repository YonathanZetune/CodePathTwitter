package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.ComposeFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        TextView createdAtTV;


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
            createdAtTV = itemView.findViewById(R.id.timeStampTV);


        }
        // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
        private String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }

        public void bind(final Tweet tweet) {
            handle.setText("@" + tweet.user.screenName);
            description.setText(tweet.body);
            createdAtTV.setText(getRelativeTimeAgo(tweet.createdAt));
            retweetsTV.setText(String.valueOf(tweet.retweetCount));
            favsTV.setText(String.valueOf(tweet.favCount));
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //bring up compose fragment with @handle prefilled
                    if (context.getClass() == TimelineActivity.class) {
                        FragmentManager fm = ((TimelineActivity) context).getSupportFragmentManager();
                        ComposeFragment editNameDialogFragment = ComposeFragment.newInstance("@" + tweet.user.screenName, false);
                        editNameDialogFragment.show(fm, "fragment_edit_name");
                    }
                }
            });
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context.getClass() == TimelineActivity.class) {
                        Intent intent = new Intent(context, TweetDetailActivity.class);
                        intent.putExtra(TweetDetailActivity.TWEET_EXTRA, Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }

                }
            });
            double width = (mediaImg.getWidth() * 0.9);
            int iwidth = ((int) width);
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
