package com.gitsearch.ui.main.recyclerview

import android.view.View
import android.view.ViewGroup
import com.gitsearch.R
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Status
import kotlinx.android.synthetic.main.list_item_loading.*

class LoadingViewHolder(
        parent: ViewGroup
) : LayoutViewHolder(parent, R.layout.list_item_loading) {

    fun bindTo(networkState: NetworkState?) {
        progress_bar.visibility = if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE
    }

}