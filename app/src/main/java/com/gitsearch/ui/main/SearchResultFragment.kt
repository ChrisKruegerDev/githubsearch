package com.gitsearch.ui.main


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.gitsearch.R
import com.gitsearch.data.model.EmptyState
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Repo
import com.gitsearch.data.remote.PER_PAGE_SIZE
import com.gitsearch.ext.setVisibleOrGone
import com.gitsearch.glide.GlideApp
import com.gitsearch.ui.BaseFragment
import com.gitsearch.ui.main.recyclerview.RepoAdapter
import kotlinx.android.synthetic.main.fragment_search_repository.*
import kotlinx.android.synthetic.main.state_layout.*
import timber.log.Timber

class SearchResultFragment : BaseFragment(R.layout.fragment_search_repository) {

    private val viewModel: MainViewModel by activityViewModel()

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provider = ViewPreloadSizeProvider<Repo>()
        val adapter = RepoAdapter(this, provider)
        adapter.setHasStableIds(true)

        recycler_view.adapter = adapter
        recycler_view.setHasFixedSize(true)
        recycler_view.addOnScrollListener(RecyclerViewPreloader(GlideApp.with(this), adapter, provider, PER_PAGE_SIZE))
        recycler_view.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))

        swipe_refresh_layout.setOnRefreshListener { viewModel.refresh() }

        viewModel.repos.observe(this, Observer {
            Timber.d("list: ${it?.size}")
            adapter.submitList(it)
        })

        viewModel.initialState.observe(this, Observer {
            Timber.d("initial state: $it")
            swipe_refresh_layout.isRefreshing = it == NetworkState.LOADING
        })

        viewModel.networkState.observe(this, Observer {
            Timber.d("network state: $it")
            adapter.setNetworkState(it)
        })

        viewModel.emptyState.observe(this, Observer {
            Timber.d("empty state: $it")
            setEmptyState(it)
        })
    }

    private fun setEmptyState(state: EmptyState?) {
        state_layout.setVisibleOrGone(state != null)
        state_button.setVisibleOrGone(state?.action != null)
        state_button.text = state?.buttonName
        state_button.setOnClickListener { state?.action?.invoke() }
        state_title.text = state?.title
        state_description.text = state?.message
        state_icon.setImageResource(state?.iconRes ?: 0)
    }

}