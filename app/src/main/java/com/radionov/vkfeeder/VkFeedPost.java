package com.radionov.vkfeeder;

import android.os.Parcel;

import com.vk.sdk.api.model.Identifiable;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrey Radionov
 */
public class VkFeedPost extends VKAttachments.VKApiAttachment implements Identifiable, android.os.Parcelable {

    public String type;

    public int source_id;

    public long date;

    public int post_id;

    public String text;

    public VkFeedAuthor feedAuthor;

    public VKAttachments attachments = new VKAttachments();

    public VkFeedPost(JSONObject from) throws JSONException {
        parse(from);
    }


    protected VkFeedPost(Parcel in) {
        type = in.readString();
        source_id = in.readInt();
        date = in.readLong();
        post_id = in.readInt();
        text = in.readString();
        feedAuthor = in.readParcelable(VkFeedAuthor.class.getClassLoader());
        attachments = in.readParcelable(VKAttachments.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(source_id);
        dest.writeLong(date);
        dest.writeInt(post_id);
        dest.writeString(text);
        dest.writeParcelable(feedAuthor, flags);
        dest.writeParcelable(attachments, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VkFeedPost> CREATOR = new Creator<VkFeedPost>() {
        @Override
        public VkFeedPost createFromParcel(Parcel in) {
            return new VkFeedPost(in);
        }

        @Override
        public VkFeedPost[] newArray(int size) {
            return new VkFeedPost[size];
        }
    };

    public VkFeedPost parse(JSONObject source) throws JSONException {
        type = source.getString("type");
        source_id = source.optInt("source_id");
        date = source.optLong("date");
        post_id = source.optInt("post_id");
        text = source.optString("text");

        attachments.fill(source.optJSONArray("attachments"));
        return this;
    }

    @Override
    public int getId() {
        return post_id;
    }

    @Override
    public CharSequence toAttachmentString() {
        return new StringBuilder(VKAttachments.TYPE_POST).append(source_id).append('_').append(post_id);
    }

    @Override
    public String getType() {
        return VKAttachments.TYPE_POST;
    }
}
