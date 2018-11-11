package com.radionov.vkfeeder;

import android.util.Log;

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
                Log.d(TAG, "parseNextPage: ");;
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

            Map<Integer, VkFeedAuthor> authors = new HashMap<>();
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
                            e.printStackTrace();
                    }
                }
            }

            if (itemsJson != null && itemsJson.length() > 0) {
                for (int i = 0; i < itemsJson.length(); i++) {
                    try {
                        VkFeedPost post = new VkFeedPost(itemsJson.getJSONObject(i));
                        post.feedAuthor = authors.get(post.source_id);
                        result.add(post);
                    } catch (Exception e) {
                        if (VKSdk.DEBUG)
                            e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
