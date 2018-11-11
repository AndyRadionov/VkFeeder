package com.radionov.vkfeeder.data.repositories;

import com.radionov.vkfeeder.data.entities.VkFeedPost;
import com.radionov.vkfeeder.data.listeners.RequestListener;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

/**
 * @author Andrey Radionov
 */
public class FeedRepository {

    private static final int PAGE_SIZE = 30;

    public void like(RequestListener requestListener, VkFeedPost feedPost) {
        VKParameters parameters = VKParameters.from(
                "type", "post",
                "owner_id", feedPost.getSourceId(),
                "item_id", feedPost.getPostId());

        VKRequest request = new VKRequest("likes.add", parameters);

        request.executeWithListener(requestListener);
    }

    public void skip(RequestListener requestListener, VkFeedPost feedPost) {
        VKParameters parameters = VKParameters.from(
                "type", "wall",
                "owner_id", feedPost.getSourceId(),
                "item_id", feedPost.getPostId());

        VKRequest request = new VKRequest("newsfeed.ignoreItem", parameters);

        request.executeWithListener(requestListener);
    }

    public void fetchFeed(RequestListener requestListener, String nextPage) {

        VKParameters parameters = VKParameters.from(
                VKApiConst.EXTENDED, 1,
                VKApiConst.COUNT, PAGE_SIZE,
                "start_from", nextPage,
                VKApiConst.FIELDS, "photo_100");

        VKRequest request = new VKRequest("newsfeed.getDiscoverForContestant", parameters);

        request.executeWithListener(requestListener);
    }
}
