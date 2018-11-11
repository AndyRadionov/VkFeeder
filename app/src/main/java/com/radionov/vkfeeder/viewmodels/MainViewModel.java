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
    private final MutableLiveData<List<VkFeedPost>> feedLiveData;
    private final MutableLiveData<Boolean> errorLiveData;
    private String nextPage;
    private boolean isConnected;

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
    }

    public MutableLiveData<List<VkFeedPost>> getFeedLiveData() {
        return feedLiveData;
    }

    public MutableLiveData<Boolean> getActionErrorLiveData() {
        return errorLiveData;
    }

    public void loadMore() {
        if (isConnected) {
            feedRepository.fetchFeed(fetchListener, nextPage);
        }
    }

    public void like(VkFeedPost feedPost) {
        if (isConnected) {
            feedRepository.like(actionListener, feedPost);
        }
    }

    public void skip(VkFeedPost feedPost) {
        if (isConnected) {
            feedRepository.like(actionListener, feedPost);
        }
    }

    public void onNetworkChange(boolean isConnected) {
        this.isConnected = isConnected;
        loadMore();
    }
}
