package com.radionov.vkfeeder.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.radionov.vkfeeder.R;
import com.radionov.vkfeeder.VkFeederApp;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import javax.inject.Inject;

/**
 * @author Andrey Radionov
 */
public abstract class BaseActivity extends AppCompatActivity {


    @Inject
    protected ViewModelProvider.Factory viewModelFactory;
    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                performLogin();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VkFeederApp.from(this).getAppComponent().inject(this);
        vkAccessTokenTracker.startTracking();

        if (!VKSdk.isLoggedIn()) {
            performLogin();
        } else {
            initViewModel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                initViewModel();
            }

            @Override
            public void onError(VKError error) {
                showError(getString(R.string.login_error));
                performLogin();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void performLogin() {
        VKSdk.login(this, VKScope.WALL);
    }

    protected void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected abstract void initViewModel();
}
