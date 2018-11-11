package com.radionov.vkfeeder.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.radionov.vkfeeder.data.entities.VkFeedPost;
import com.radionov.vkfeeder.data.listeners.RequestListener;
import com.radionov.vkfeeder.data.repositories.FeedRepository;
import com.radionov.vkfeeder.utils.FeedPostsParser;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKResponse;

import java.util.List;

/**
 * @author Andrey Radionov
 */
public class MainViewModel extends ViewModel {

    private final FeedRepository feedRepository;
    private MutableLiveData<List<VkFeedPost>> feedLiveData;
    private MutableLiveData<Boolean> errorLiveData;
    private String nextPage;
    private final RequestListener fetchListener = new RequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            List<VkFeedPost> feedPosts = FeedPostsParser.parseData(response.json);
            feedLiveData.postValue(feedPosts);
            nextPage = FeedPostsParser.parseNextPage(response.json);
        }
    };

    private final RequestListener actionListener = new RequestListener() {
        @Override
        public void onError(VKError error) {
            errorLiveData.postValue(true);
        }
    };

    public MainViewModel(FeedRepository repository) {
        feedRepository = repository;
        feedLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        feedRepository.fetchFeed(fetchListener, nextPage);
    }

    public MutableLiveData<List<VkFeedPost>> getFeedLiveData() {
        return feedLiveData;
    }

    public MutableLiveData<Boolean> getActionErrorLiveData() {
        return errorLiveData;
    }

    public void loadMore() {
        feedRepository.fetchFeed(fetchListener, nextPage);
    }

    public void like(VkFeedPost feedPost) {
        feedRepository.like(actionListener, feedPost);
    }

    public void skip(VkFeedPost feedPost) {
        feedRepository.like(actionListener, feedPost);
    }
}
