package com.gitsearch.data.remote.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.content.Context
import com.gitsearch.data.model.Repo
import com.gitsearch.data.remote.GitHub

class RepoDataSourceFactory(
        private val context: Context,
        private val gitHub: GitHub,
        private val query: String
) : DataSource.Factory<Int, Repo>() {

    val repoDataSource = MutableLiveData<RepoDataSource>()

    override fun create(): DataSource<Int, Repo> {
        val source = RepoDataSource(context, gitHub, query)
        repoDataSource.postValue(source)
        return source
    }

}
