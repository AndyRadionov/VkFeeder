package com.radionov.vkfeeder;

import android.app.Application;
import android.content.Context;

import com.radionov.vkfeeder.di.AppComponent;
import com.radionov.vkfeeder.di.DaggerAppComponent;
import com.vk.sdk.VKSdk;

/**
 * @author Andrey Radionov
 */
public class VkFeederApp extends Application {

    private AppComponent appComponent;

    public static VkFeederApp from(Context context) {
        return (VkFeederApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
        appComponent = DaggerAppComponent.create();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
