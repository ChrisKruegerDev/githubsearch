package com.gitsearch.data.remote

import com.gitsearch.data.model.Repo
import com.gitsearch.data.model.SearchResult
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubSearchService {

    /**
     * Example: https://api.github.com/search/repositories?q=topic
     *
     * @see [Search](https://developer.github.com/v3/search)
     */
    @GET("search/repositories")
    fun repositories(
            @Query(value = "q", encoded = true) query: String,
            @Query("page") page: Int,
            @Query("per_page") perPage: Int
    ): Observable<Result<SearchResult<Repo>>>

}
