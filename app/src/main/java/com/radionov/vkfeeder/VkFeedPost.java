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

    private String type;
    private int sourceId;
    private long date;
    private int postId;
    private String text;
    private VkFeedAuthor feedAuthor;
    private VKAttachments attachments = new VKAttachments();

    public VkFeedPost(JSONObject from) throws JSONException {
        parse(from);
    }


    protected VkFeedPost(Parcel in) {
        type = in.readString();
        sourceId = in.readInt();
        date = in.readLong();
        postId = in.readInt();
        text = in.readString();
        feedAuthor = in.readParcelable(VkFeedAuthor.class.getClassLoader());
        attachments = in.readParcelable(VKAttachments.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(sourceId);
        dest.writeLong(date);
        dest.writeInt(postId);
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
        sourceId = source.optInt("source_id");
        date = source.optLong("date");
        postId = source.optInt("post_id");
        text = source.optString("text");

        attachments.fill(source.optJSONArray("attachments"));
        return this;
    }

    @Override
    public int getId() {
        return postId;
    }

    @Override
    public CharSequence toAttachmentString() {
        return new StringBuilder(VKAttachments.TYPE_POST).append(sourceId).append('_').append(postId);
    }

    @Override
    public String getType() {
        return VKAttachments.TYPE_POST;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public VkFeedAuthor getFeedAuthor() {
        return feedAuthor;
    }

    public void setFeedAuthor(VkFeedAuthor feedAuthor) {
        this.feedAuthor = feedAuthor;
    }

    public VKAttachments getAttachments() {
        return attachments;
    }

    public void setAttachments(VKAttachments attachments) {
        this.attachments = attachments;
    }
}
