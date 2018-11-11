package com.radionov.vkfeeder.utils;

import android.util.Log;
import android.util.SparseArray;

import com.radionov.vkfeeder.data.entities.VkFeedAuthor;
import com.radionov.vkfeeder.data.entities.VkFeedPost;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey Radionov
 */
public class FeedPostsParser {
    private static final String TAG = FeedPostsParser.class.getSimpleName();
    private static final String EMPTY_STRING = "";

    private FeedPostsParser() {
    }

    public static String parseNextPage(JSONObject response) {

        if (response.has("response")) {
            try {
                return response.optJSONObject("response").getString("next_from");
            } catch (JSONException e) {
                Log.d(TAG, "parseNextPage: " + e);
            }
        }
        return EMPTY_STRING;
    }

    public static List<VkFeedPost> parseData(JSONObject response) {

        List<VkFeedPost> result = new ArrayList<>();
        if (response.has("response")) {

            JSONObject source = response.optJSONObject("response");
            JSONArray itemsJson = source.optJSONArray("items");
            JSONArray usersJson = source.optJSONArray("profiles");
            JSONArray groupsJson = source.optJSONArray("groups");

            SparseArray<VkFeedAuthor> authors = new SparseArray<>();
            if (usersJson != null && usersJson.length() > 0) {
                for (int i = 0; i < usersJson.length(); i++) {
                    try {
                        VKApiUser user = new VKApiUser(usersJson.getJSONObject(i));
                        VkFeedAuthor author = new VkFeedAuthor((user.first_name + " " + user.last_name), user.photo_100);
                        authors.put(user.id, author);
                    } catch (Exception e) {
                        if (VKSdk.DEBUG)
                            e.printStackTrace();
                    }
                }
            }

            if (groupsJson != null && groupsJson.length() > 0) {
                for (int i = 0; i < groupsJson.length(); i++) {
                    try {
                        VKApiCommunity group = new VKApiCommunity(groupsJson.getJSONObject(i));
                        VkFeedAuthor author = new VkFeedAuthor(group.name, group.photo_100);
                        authors.put(group.id * -1, author);
                    } catch (Exception e) {
                        if (VKSdk.DEBUG)
                            Log.d(TAG, "parseData: " + e);
                    }
                }
            }

            if (itemsJson != null && itemsJson.length() > 0) {
                for (int i = 0; i < itemsJson.length(); i++) {
                    try {
                        VkFeedPost post = new VkFeedPost(itemsJson.getJSONObject(i));
                        post.setFeedAuthor(authors.get(post.getSourceId()));
                        result.add(post);
                    } catch (Exception e) {
                        if (VKSdk.DEBUG)
                            Log.d(TAG, "parseData: " + e);
                    }
                }
            }
        }
        return result;
    }
}
