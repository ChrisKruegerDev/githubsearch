package com.gitsearch.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gitsearch.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class GlideUtils {

  private static final int AVATAR_THUMB_WIDTH = 40;
  private static final int AVATAR_THUMB_HEIGHT = 40;

  private static final RequestOptions OPTIONS_CIRCLE = new RequestOptions().circleCrop().dontAnimate();
  private static final RequestOptions OPTIONS_CIRCLE_THUMB = new RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.DATA);

  public static <E> GlideRequest<Drawable> getAvatar(Context context, GlideRequests requests) {
    Drawable drawable = context.getResources().getDrawable(R.drawable.ic_account_circle_people, context.getTheme());
    return requests.asDrawable().apply(OPTIONS_CIRCLE).placeholder(drawable).error(drawable);
  }

  public static GlideRequest<Drawable> getAvatarPreload(GlideRequests requests) {
    return requests.asDrawable().apply(OPTIONS_CIRCLE_THUMB).override(AVATAR_THUMB_WIDTH, AVATAR_THUMB_HEIGHT).transition(withCrossFade());
  }

}
