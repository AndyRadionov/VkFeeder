package com.radionov.vkfeeder.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * @author Andrey Radionov
 */
public class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        return connectivityManager.getActiveNetworkInfo() != null
                        && connectivityManager.getActiveNetworkInfo().isAvailable()
                        && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
