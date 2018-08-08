package com.gitsearch.ui.main.recyclerview

import android.arch.paging.PagedListAdapter
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.gitsearch.R
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Repo
import com.gitsearch.glide.GlideApp
import com.gitsearch.glide.GlideRequest
import com.gitsearch.glide.GlideRequests
import com.gitsearch.glide.GlideUtils

private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {

    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
            oldItem.fullName == newItem.fullName

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
            oldItem == newItem

}

private const val LOADING_ID = R.layout.list_item_loading.toLong()

class RepoAdapter(
        fragment: Fragment,
        private val preloadSizeProvider: ViewPreloadSizeProvider<Repo>
) : PagedListAdapter<Repo, RecyclerView.ViewHolder>(REPO_COMPARATOR), ListPreloader.PreloadModelProvider<Repo> {

    private val requests: GlideRequests = GlideApp.with(fragment)
    private val fullRequest: GlideRequest<Drawable> = GlideUtils.getAvatar(fragment.requireContext(), requests)
    private val thumbRequest: GlideRequest<Drawable> = GlideUtils.getAvatarPreload(requests)
    private val preloadRequest: GlideRequest<Drawable> = thumbRequest.clone().priority(Priority.HIGH)

    private var networkState: NetworkState? = null

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is ImageViewHolder)
            requests.clear(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            R.layout.list_item_loading    -> LoadingViewHolder(parent)
            R.layout.list_item_repository -> RepoViewHolder(parent)
            else                          -> throw IllegalArgumentException("unknown view type $viewType")
        }

        if (holder is ImageViewHolder)
            preloadSizeProvider.setView(holder.image)

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RepoViewHolder    -> {
                val item = getItem(position)
                holder.bindTo(item)
                val avatarUrl = item?.owner?.avatarUrl
                fullRequest.thumbnail(thumbRequest.load(avatarUrl)).load(avatarUrl).into(holder.image)
            }
            is LoadingViewHolder -> holder.bindTo(networkState)
        }
    }

    override fun getPreloadItems(position: Int): MutableList<Repo> {
        val list = currentList

        val listPosition = if (isLoading()) position - 1 else position

        return if (listPosition < 0 || list == null || listPosition >= list.size)
            mutableListOf()
        else
            list.subList(listPosition, listPosition + 1)
    }

    override fun getPreloadRequestBuilder(
            item: Repo
    ): RequestBuilder<Drawable> = preloadRequest.load(item.owner?.avatarUrl)

    override fun getItemId(
            position: Int
    ): Long = if (isLoading() && position == itemCount - 1)
        LOADING_ID
    else
        getItem(position)?.id ?: super.getItemId(position)

    override fun getItemCount(): Int = super.getItemCount() + if (isLoading()) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (isLoading() && position == itemCount - 1)
            R.layout.list_item_loading
        else
            R.layout.list_item_repository
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val prevNetworkState = networkState
        val isLoading = isLoading()
        networkState = newNetworkState

        if (isLoading != isLoading()) {
            if (isLoading)
                notifyItemRemoved(super.getItemCount())
            else
                notifyItemInserted(super.getItemCount())
        } else if (isLoading && prevNetworkState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun isLoading() = networkState == NetworkState.LOADING


}
