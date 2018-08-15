package com.gitsearch.ui.main

import android.app.Application
import android.arch.lifecycle.*
import android.arch.paging.PagedList
import android.content.res.Resources
import com.gitsearch.R
import com.gitsearch.data.model.EmptyState
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Repo
import com.gitsearch.data.model.Status
import com.gitsearch.data.remote.GithubSearchRepository
import com.gitsearch.data.remote.paging.RepoSearchResult
import com.gitsearch.ext.isOnline
import javax.inject.Inject

class MainViewModel @Inject constructor(
        application: Application,
        private val resources: Resources,
        private val githubSearchRepository: GithubSearchRepository
) : AndroidViewModel(application) {

    private val query = MutableLiveData<String>()
    private val repoSearchResult: LiveData<RepoSearchResult> = Transformations.map(query) {
        githubSearchRepository.search(it)
    }

    val repos: LiveData<PagedList<Repo>> = Transformations.switchMap(repoSearchResult) { it -> it.data }
    val networkState: LiveData<NetworkState> = Transformations.switchMap(repoSearchResult) { it -> it.networkState }
    val initialState: LiveData<NetworkState> = Transformations.switchMap(repoSearchResult) { it -> it.initialState }
    val emptyState: MediatorLiveData<EmptyState?> = MediatorLiveData()

    private val initialSearch by lazy {
        EmptyState(
                resources.getString(R.string.search_title),
                resources.getString(R.string.search_description),
                R.drawable.ic_search_48,
                resources.getString(R.string.button_search)
        )
    }

    private val offlineState by lazy {
        EmptyState(
                resources.getString(R.string.error_offline),
                resources.getString(R.string.error_offline_description),
                R.drawable.ic_cloud_off_48,
                resources.getString(R.string.button_retry),
                ::retry
        )
    }

    private val errorState by lazy {
        EmptyState(
                resources.getString(R.string.error_loading_repositories_title),
                resources.getString(R.string.error_loading_repositories_description),
                R.drawable.ic_sentiment_dissatisfied_48,
                resources.getString(R.string.button_retry),
                ::retry
        )
    }

    private val noResultsState by lazy {
        EmptyState(
                resources.getString(R.string.error_no_repositories),
                resources.getString(R.string.error_no_repositories_description),
                R.drawable.ic_search_48
        )
    }

    init {
        emptyState.value = initialSearch

        emptyState.addSource(initialState) {
            if (it == null) return@addSource

            val list = repos.value
            if ((list == null || list.isEmpty()) && it.status == Status.RUNNING)
                emptyState.value = null
        }

        emptyState.addSource(query) {
            if (it == null || it.isEmpty())
                emptyState.value = initialSearch
        }

        emptyState.addSource(repos) {
            if (it != null && it.isNotEmpty()) {
                emptyState.value = null
                return@addSource
            }

            val status = initialState.value?.status
            if (status == Status.FAILED)
                emptyState.value = if (!application.isOnline) offlineState else errorState
            else if (status == Status.SUCCESS)
                emptyState.value = noResultsState
        }
    }

    fun startSearch(value: String) {
        query.postValue(value)
    }

    fun refresh() {
        repoSearchResult.value?.refresh?.invoke()
    }

    private fun retry() {
        repoSearchResult.value?.retry?.invoke()
    }

}
