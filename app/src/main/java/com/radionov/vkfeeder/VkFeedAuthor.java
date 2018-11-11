package com.radionov.vkfeeder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Andrey Radionov
 */
public class VkFeedAuthor implements Parcelable {

    private String name;
    private String photo;

    public VkFeedAuthor(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    protected VkFeedAuthor(Parcel in) {
        name = in.readString();
        photo = in.readString();
    }

    public static final Creator<VkFeedAuthor> CREATOR = new Creator<VkFeedAuthor>() {
        @Override
        public VkFeedAuthor createFromParcel(Parcel in) {
            return new VkFeedAuthor(in);
        }

        @Override
        public VkFeedAuthor[] newArray(int size) {
            return new VkFeedAuthor[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(photo);
    }
}
