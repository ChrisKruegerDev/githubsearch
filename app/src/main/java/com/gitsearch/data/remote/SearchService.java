package com.gitsearch.data.remote;

import com.gitsearch.data.model.Repository;
import com.gitsearch.data.model.SearchResult;
import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {

    /**
     * Example: https://api.github.com/search/repositories?q=topic
     *
     * @see <a href="https://developer.github.com/v3/search">Search</a>
     */
    @GET("search/repositories")
    Observable<Result<SearchResult<Repository>>> repositories(@Query(value = "q", encoded = true) String query, @Query("page") int page);

}
