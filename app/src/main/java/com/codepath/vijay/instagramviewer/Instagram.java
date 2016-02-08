package com.codepath.vijay.instagramviewer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by uttamavillain on 2/6/16.

 */
public class Instagram {
    public static final String TAG = Instagram.class.getName();

    private String imageUrl;
    private String videoUrl;
    private String caption;
    private User user;
    private int likeCount;
    private ArrayList<User> likes = new ArrayList<User>();
    private int commentCount;
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    private boolean isImage;
    private String locationText;
    private String locationLat;
    private String locationLng;
    private boolean isLocationAvail;

    public Instagram(String imageUrl, String videoUrl, String caption, User user, int likeCount, ArrayList<User> likes, int commentCount,
                     ArrayList<Comment> comments,
                     Boolean isImage,
                     String locationText,
                     String locationLat,
                     String locationLng,
                     boolean isLocationAvail) {
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.user = user;
        this.likeCount = likeCount;
        this.likes = likes;
        this.commentCount = commentCount;
        this.comments = comments;
        this.isImage = isImage;
        this.locationText = locationText;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.isLocationAvail = isLocationAvail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public boolean isLocationAvail() {
        return isLocationAvail;
    }

    public static Instagram fromJson(JSONObject jsonObject) {
        Instagram ret = null;

        try {
            Boolean isImage = jsonObject.getString("type").equals("image");
            String imageUrl = jsonObject.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
            String videoUrl = isImage?null:jsonObject.getJSONObject("videos").getJSONObject("standard_resolution").getString("url");
            String caption = jsonObject.getJSONObject("caption").getString("text");
            User user = getUserObject(jsonObject.getJSONObject("user"));
            int likeCount = jsonObject.getJSONObject("likes").getInt("count");
            ArrayList<User> likes = getLikesUserList(jsonObject.getJSONObject("likes").getJSONArray("data"));
            int commentCount = jsonObject.getJSONObject("comments").getInt("count");
            ArrayList<Comment> comments = getCommentsList(jsonObject.getJSONObject("comments").getJSONArray("data"));
            boolean isLocationAvail = false;
            JSONObject locaton = null;
            try {
                locaton = jsonObject.getJSONObject("location");
                isLocationAvail = true;
                Log.d(TAG, "location available");
            } catch(JSONException e) {
            }
            String locationText = isLocationAvail?locaton.getString("name"):null;
            String locationLat = isLocationAvail?locaton.getString("latitude"):null;
            String locationLng = isLocationAvail?locaton.getString("longitude"):null;
            ret = new Instagram(imageUrl, videoUrl, caption, user, likeCount, likes, commentCount, comments, isImage,
                    locationText,
                    locationLat,
                    locationLng,
                    isLocationAvail);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return ret;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public static ArrayList<Instagram> fromJson(JSONArray jsonArray) {
        ArrayList<Instagram> Instagrames = new ArrayList<Instagram>(jsonArray.length());
        // Process each result in json array, decode and convert to Instagram object
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject InstagramJson = null;
            try {
                InstagramJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Instagram Instagram = fromJson(InstagramJson);
            if (Instagram != null) {
                Instagrames.add(Instagram);
            }
        }

        return Instagrames;
    }

    private static ArrayList<Comment> getCommentsList(JSONArray jsonArray) {
        ArrayList<Comment> ret = new ArrayList<Comment>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject userObject = null;
            String comment_text = "";
            String comment_date = null;
            try {
                comment_text = jsonArray.getJSONObject(i).getString("text");
                comment_date = jsonArray.getJSONObject(i).getString("created_time");
                userObject = jsonArray.getJSONObject(i).getJSONObject("from");
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Comment comment = new Comment(comment_text, getUserObject(userObject), comment_date);
            if (comment != null) {
                ret.add(0,comment);
            }
        }

        return ret;
    }

    private static ArrayList<User> getLikesUserList(JSONArray jsonArray) {
        ArrayList<User> ret = new ArrayList<User>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject userObject = null;
            try {
                userObject = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            User user = getUserObject(userObject);
            if (user != null) {
                ret.add(user);
            }
        }

        return ret;
    }

    private static User getUserObject(JSONObject jsonObject) {
        try {
            return new User(
                    jsonObject.getString("profile_picture"),
                    jsonObject.getString("full_name"),
                    jsonObject.getString("username")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCaption() {
        return caption;
    }

    public User getUser() {
        return user;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public ArrayList<User> getLikes() {
        return likes;
    }

    @Override
    public String toString() {
        return "\n"+user.getFullName()+" "+caption+" "+imageUrl+" "+isLocationAvail;
    }

    public boolean isImage() {
        return isImage;
    }
}
