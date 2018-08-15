package com.gitsearch.ext

import android.content.Context
import android.net.ConnectivityManager

val Context.isOnline: Boolean
    get() = connectivityManager.activeNetworkInfo?.isConnected ?: false


private val Context.connectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
