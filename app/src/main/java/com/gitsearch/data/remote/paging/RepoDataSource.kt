package com.gitsearch.data.remote.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.content.Context
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Repo
import com.gitsearch.data.remote.GitHub
import com.gitsearch.data.remote.PER_PAGE_SIZE
import timber.log.Timber

class RepoDataSource(
        private val context: Context,
        private val gitHub: GitHub,
        private val query: String
) : PageKeyedDataSource<Int, Repo>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Int>, callback: PageKeyedDataSource.LoadInitialCallback<Int, Repo>) {
        Timber.d("initial start")
        initialState.postValue(NetworkState.LOADING)

        gitHub.search()
                .repositories(query, 1, PER_PAGE_SIZE)
                .compose(gitHub.applyRateLimit())
                .flatMap(PageableMapper())
                .blockingSubscribe(
                        {
                            Timber.d("initial result")
                            callback.onResult(it.value?.items ?: emptyList(), null, it.next)
                        },
                        {
                            Timber.d("initial error")
                            initialState.postValue(NetworkState.error(it))
                        },
                        {
                            Timber.d("initial complete")
                            initialState.postValue(NetworkState.LOADED)
                        }
                )
    }

    override fun loadBefore(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, Repo>) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, Repo>) {
        Timber.d("next page: ${params.key}")

        networkState.postValue(NetworkState.LOADING)

        gitHub.search()
                .repositories(query, params.key, PER_PAGE_SIZE)
                .compose(gitHub.applyRateLimit())
                .flatMap(PageableMapper())
                .blockingSubscribe(
                        { result ->
                            callback.onResult(result.value?.items ?: emptyList(), result.next)
                        },
                        {
                            networkState.postValue(NetworkState.error(it))
                        },
                        {
                            networkState.postValue(NetworkState.LOADED)
                        }
                )
    }

}
