package com.codepath.vijay.instagramviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by uttamavillain on 2/6/16.
 */
public class User implements Parcelable {
    private String profilePic;
    private String fullName;
    private String userName;

    public User(String profilePic, String fullName, String userName) {
        this.profilePic = profilePic;
        this.fullName = fullName;
        this.userName = userName;
    }

    protected User(Parcel in) {
        profilePic = in.readString();
        fullName = in.readString();
        userName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getProfilePic() {
        return profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePic);
        dest.writeString(fullName);
        dest.writeString(userName);
    }

    @Override
    public String toString() {
        return "\n"+getFullName();
    }
}
