package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {

    @ColumnInfo
    public String userName;
    @ColumnInfo
    public String screenName;
    @ColumnInfo
    public String imageURL;
    @ColumnInfo
    @PrimaryKey
    public long id;

    //Parcel library needs this empty constructor
    public User() {

    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.userName = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.imageURL = jsonObject.getString("profile_image_url_https");
        user.id = jsonObject.getLong("id");
        return user;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).user);

        }
        return users;
    }
}
