package com.radionov.vkfeeder.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.radionov.vkfeeder.R;
import com.radionov.vkfeeder.VkFeederApp;
import com.radionov.vkfeeder.utils.NetworkUtils;
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

    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onNetworkChange(NetworkUtils.isInternetAvailable(BaseActivity.this));
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
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStatusReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStatusReceiver);
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

    protected void showNoConnection() {
        Snackbar.make(findViewById(android.R.id.content),
                R.string.no_internet, Snackbar.LENGTH_LONG)
                .show();
    }

    protected abstract void initViewModel();

    protected abstract void onNetworkChange(boolean isConnected);
}
