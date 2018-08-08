package com.gitsearch.data.remote

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.gitsearch.data.remote.paging.RepoDataSourceFactory
import com.gitsearch.data.remote.paging.RepoSearchResult
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

const val PER_PAGE_SIZE = 20
const val PREFETCH_SIZE = 10

@Singleton
class GithubSearchRepository @Inject constructor(
        private val gitHub: GitHub,
        private val networkExecutor: Executor
) {

    fun search(query: String): RepoSearchResult {
        val dataSourceFactory = RepoDataSourceFactory(gitHub, query, networkExecutor)

        val config = PagedList.Config.Builder()
                .setPageSize(PER_PAGE_SIZE)
                .setPrefetchDistance(PREFETCH_SIZE)
                .setInitialLoadSizeHint(PER_PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build()

        val data = LivePagedListBuilder(dataSourceFactory, config)
                .setFetchExecutor(networkExecutor)
                .build()

        val networkState = Transformations.switchMap(dataSourceFactory.repoDataSource) { it.networkState }
        val initialState = Transformations.switchMap(dataSourceFactory.repoDataSource) { it.initialState }

        return RepoSearchResult(
                data,
                networkState,
                initialState,
                { dataSourceFactory.repoDataSource.value?.invalidate() },
                { dataSourceFactory.repoDataSource.value?.retryAllFailed() }
        )
    }

}
