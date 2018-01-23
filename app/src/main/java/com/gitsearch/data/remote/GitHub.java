package com.gitsearch.data.remote;

import android.support.annotation.NonNull;

import com.gitsearch.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Singleton
public class GitHub {

  public static final String API_HOST = "api.github.com";
  public static final String API_URL = "https://" + API_HOST;

  public static final String HEADER_LINK = "Link";
  public static final String HEADER_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";

  public static final String PARAMETER_PAGE = "page";

  private static final int MAX_RATE_LIMIT_MILLIS = 60 * 1000;

  private final Gson gson;
  private OkHttpClient okHttpClient;
  private Retrofit retrofit;

  private final AtomicLong lastCheckedTime = new AtomicLong(0);
  private final AtomicInteger currentRateLimit = new AtomicInteger(0);

  @Inject
  public GitHub(Gson gson) {
    this.gson = gson;
  }

  public SearchService search() {
    return retrofit().create(SearchService.class);
  }

  /**
   * For unauthenticated requests, the rate limit allows the app to make up to 10 requests per minute,
   * otherwise a HTTP 429 error will be thrown.
   * <p>
   * The request will wait 10 seconds, if the time span to the last check is lesser then 10 seconds
   * and the remain limit is 0.
   */
  public <T> ObservableTransformer<T, T> applyRateLimit() {
    return upstream -> {
      int rateLimit = currentRateLimit.get();
      long checkedTime = lastCheckedTime.get();
      if (rateLimit == 0 && System.currentTimeMillis() - checkedTime <= MAX_RATE_LIMIT_MILLIS) {
        Timber.i("time: %d limit: %d", checkedTime, rateLimit);
        return Observable.timer(MAX_RATE_LIMIT_MILLIS, TimeUnit.MILLISECONDS).flatMap(val -> upstream);
      }
      else {
        return upstream;
      }
    };
  }

  private Retrofit retrofit() {
    if (retrofit == null) {
      retrofit = new Retrofit.Builder()
        .baseUrl(API_URL)
        .client(okHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
    }
    return retrofit;
  }

  private OkHttpClient okHttpClient() {
    if (okHttpClient == null) {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.connectTimeout(60, TimeUnit.SECONDS);
      builder.readTimeout(60, TimeUnit.SECONDS);
      builder.writeTimeout(60, TimeUnit.SECONDS);

      if (BuildConfig.DEBUG) {
        // Enable logging for debug builds
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
      }

      builder.addNetworkInterceptor(new RateLimitInterceptor());

      okHttpClient = builder.build();
    }
    return okHttpClient;
  }

  private class RateLimitInterceptor implements okhttp3.Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
      Request request = chain.request();
      Response response = chain.proceed(request);
      if (response.isSuccessful()) {
        Headers headers = response.headers();
        try {
          String rateLimit = headers.get(HEADER_RATE_LIMIT_REMAINING);
          currentRateLimit.set(Integer.valueOf(rateLimit));
          lastCheckedTime.set(System.currentTimeMillis());
        }
        catch (NumberFormatException e) {
          Timber.e(e);
        }
      }

      return response;
    }
  }
}
