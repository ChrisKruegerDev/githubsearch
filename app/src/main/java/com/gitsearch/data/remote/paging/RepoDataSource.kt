package com.gitsearch.data.remote.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Repo
import com.gitsearch.data.remote.GitHub
import com.gitsearch.data.remote.PER_PAGE_SIZE
import timber.log.Timber
import java.util.concurrent.Executor

class RepoDataSource(
        private val gitHub: GitHub,
        private val query: String,
        private val retryExecutor: Executor
) : PageKeyedDataSource<Int, Repo>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null
    val networkState = MutableLiveData<NetworkState>()
    val initialState = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null

        if (prevRetry != null)
            retryExecutor.execute { prevRetry() }
    }

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
                            retry = { loadInitial(params, callback) }
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
                            Timber.d("load after result")
                            callback.onResult(result.value?.items ?: emptyList(), result.next)
                        },
                        {
                            Timber.d("load after error")
                            retry = { loadAfter(params, callback) }
                            networkState.postValue(NetworkState.error(it))
                        },
                        {
                            Timber.d("load after complete")
                            networkState.postValue(NetworkState.LOADED)
                        }
                )
    }

}
