package com.radionov.vkfeeder.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.radionov.vkfeeder.data.repositories.FeedRepository;

/**
 * @author Andrey Radionov
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final FeedRepository feedRepository;

    public ViewModelFactory(FeedRepository repository) {
        feedRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(feedRepository);
        } else {
            throw new IllegalArgumentException("ViewModel type was not found");
        }
    }
}
