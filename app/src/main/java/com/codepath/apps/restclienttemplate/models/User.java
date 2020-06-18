package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String userName;
    public String screenName;
    public String imageURL;

    //Parcel library needs this empty constructor
    public User() {

    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.userName = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.imageURL = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
