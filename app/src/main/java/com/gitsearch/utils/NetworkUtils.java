package com.gitsearch.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

public class NetworkUtils {

  public static boolean isOnline(Context context) {
    NetworkInfo activeNetwork = getNetworkInfo(context);
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }

  @Nullable
  private static NetworkInfo getNetworkInfo(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm == null ? null : cm.getActiveNetworkInfo();
  }

}
