package com.gitsearch.ui.main.recyclerview

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

abstract class LayoutViewHolder constructor(
        final override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    constructor(
            parent: ViewGroup,
            @LayoutRes resource: Int
    ) : this(LayoutInflater.from(parent.context).inflate(resource, parent, false))

    val context: Context
        get() = containerView.context

}
