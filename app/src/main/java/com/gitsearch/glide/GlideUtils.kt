package com.gitsearch.glide

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gitsearch.R

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

object GlideUtils {

    private const val AVATAR_THUMB_WIDTH = 40
    private const val AVATAR_THUMB_HEIGHT = 40

    private val optionsCircle = RequestOptions().circleCrop().dontAnimate()
    private val optionsCircleThumb = RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.DATA)

    fun getAvatar(context: Context, requests: GlideRequests): GlideRequest<Drawable> {
        val drawable = context.resources.getDrawable(R.drawable.ic_account_circle_people, context.theme)

        return requests.asDrawable()
                .apply(optionsCircle)
                .placeholder(drawable)
                .error(drawable)
    }

    fun getAvatarPreload(requests: GlideRequests): GlideRequest<Drawable> {
        return requests.asDrawable()
                .apply(optionsCircleThumb)
                .override(AVATAR_THUMB_WIDTH, AVATAR_THUMB_HEIGHT)
                .transition(withCrossFade())
    }

}
