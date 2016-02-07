package com.codepath.vijay.instagramviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by uttamavillain on 2/6/16.
 */
public class Comment implements Parcelable{
    private String comment;
    private User user;
    private String date;

    public Comment(String comment, User user, String date) {
        this.comment = comment;
        this.user = user;
        this.date = date;
    }

    protected Comment(Parcel in) {
        comment = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        date = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeParcelable(user, flags);
        dest.writeString(date);
    }
}
