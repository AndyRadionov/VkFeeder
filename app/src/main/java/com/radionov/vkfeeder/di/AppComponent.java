package com.radionov.vkfeeder.di;

import com.radionov.vkfeeder.ui.common.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Andrey Radionov
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(BaseActivity baseActivity);
}
