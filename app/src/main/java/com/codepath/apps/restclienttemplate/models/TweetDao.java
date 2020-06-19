package com.codepath.apps.restclienttemplate.models;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT Tweet.body AS tweet_body,Tweet.createdAt AS tweet_createdAt,Tweet.id AS tweet_id," +
            "Tweet.mediaURL AS tweet_mediaURL,Tweet.retweetCount AS tweet_retweetCount,Tweet.favCount AS tweet_favCount," +
            " User.* FROM Tweet INNER JOIN User ON Tweet.userId  = User.id ORDER BY Tweet.createdAt DESC LIMIT 300")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
