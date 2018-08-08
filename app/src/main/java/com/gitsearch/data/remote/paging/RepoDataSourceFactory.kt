package com.gitsearch.data.remote.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.gitsearch.data.model.Repo
import com.gitsearch.data.remote.GitHub
import java.util.concurrent.Executor

class RepoDataSourceFactory(
        private val gitHub: GitHub,
        private val query: String,
        private val retryExecutor: Executor
) : DataSource.Factory<Int, Repo>() {

    val repoDataSource = MutableLiveData<RepoDataSource>()

    override fun create(): DataSource<Int, Repo> {
        val source = RepoDataSource(gitHub, query, retryExecutor)
        repoDataSource.postValue(source)
        return source
    }

}
