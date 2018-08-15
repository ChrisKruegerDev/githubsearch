package com.gitsearch.ext

import android.view.View

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if(visible) View.VISIBLE else View.GONE
}

fun View.setVisibleOrInvisible(visible: Boolean) {
    visibility = if(visible) View.VISIBLE else View.INVISIBLE
}