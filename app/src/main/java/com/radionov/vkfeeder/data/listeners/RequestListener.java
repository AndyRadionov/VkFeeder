package com.radionov.vkfeeder.data.listeners;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

/**
 * @author Andrey Radionov
 */
public class RequestListener extends VKRequest.VKRequestListener {

    @Override
    public void onComplete(VKResponse response) {
    }

    @Override
    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
    }

    @Override
    public void onError(VKError error) {
    }
}
