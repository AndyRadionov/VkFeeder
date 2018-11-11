package com.radionov.vkfeeder.di;

import android.arch.lifecycle.ViewModelProvider;


import com.radionov.vkfeeder.data.repositories.FeedRepository;
import com.radionov.vkfeeder.viewmodels.ViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrey Radionov
 */
@Module
public class AppModule {

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(FeedRepository repository) {
        return new ViewModelFactory(repository);
    }

    @Singleton
    @Provides
    FeedRepository provideFeedRepository() {
        return new FeedRepository();
    }
}
