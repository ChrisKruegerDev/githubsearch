package com.gitsearch.data.remote

import com.gitsearch.BuildConfig
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

private const val API_HOST = "api.github.com"
private const val API_URL = "https://$API_HOST"
private const val HEADER_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining"
private const val MAX_RATE_LIMIT_MILLIS = 60 * 1000

@Singleton
class GitHub @Inject constructor(private val gson: Gson) {

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(60, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            // Enable logging for debug builds
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        }

        builder.addNetworkInterceptor(RateLimitInterceptor())

        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private val lastCheckedTime = AtomicLong(0)
    private val currentRateLimit = AtomicInteger(0)

    fun search(): GithubSearchService = retrofit.create(GithubSearchService::class.java)


    /**
     * For unauthenticated requests, the rate limit allows the app to make up to 10 requests per minute,
     * otherwise a HTTP 429 error will be thrown.
     *
     *
     * The request will wait 10 seconds, if the time span to the last check is lesser then 10 seconds
     * and the remain limit is 0.
     */
    fun <T> applyRateLimit(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            val rateLimit = currentRateLimit.get()
            val checkedTime = lastCheckedTime.get()
            val needsRateLimit = rateLimit == 0 && System.currentTimeMillis() - checkedTime <= MAX_RATE_LIMIT_MILLIS

            return@ObservableTransformer if (needsRateLimit) {
                Timber.i("time: %d limit: %d", checkedTime, rateLimit)
                Observable.timer(MAX_RATE_LIMIT_MILLIS.toLong(), TimeUnit.MILLISECONDS).flatMap { upstream }
            }
            else {
                upstream
            }
        }
    }

    private inner class RateLimitInterceptor : okhttp3.Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            if (response.isSuccessful) {
                val rateLimit = response.headers().get(HEADER_RATE_LIMIT_REMAINING)
                currentRateLimit.set(rateLimit?.toIntOrNull() ?: 0)
                lastCheckedTime.set(System.currentTimeMillis())
            }

            return response
        }

    }

}
