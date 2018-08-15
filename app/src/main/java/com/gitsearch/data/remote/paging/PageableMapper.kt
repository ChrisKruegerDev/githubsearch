package com.gitsearch.data.remote.paging

import com.gitsearch.data.model.Pageable
import com.gitsearch.data.model.PaginationLink
import com.gitsearch.data.model.RelType
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import retrofit2.adapter.rxjava2.Result

private const val HEADER_LINK = "Link"


class PageableMapper<T> : Function<Result<T>, ObservableSource<out Pageable<T>>> {

    override fun apply(result: Result<T>): ObservableSource<out Pageable<T>> {
        if (result.isError) {
            val throwable = result.error()

            return if (throwable == null)
                Observable.error(RuntimeException("throwable == null"))
            else
                Observable.error(throwable)
        }

        val response = result.response() ?: return Observable.error(IllegalStateException("response == null"))

        val link = response.headers()?.get(HEADER_LINK)
        val value = response.body()
        val pageable = createPageable(link, value)

        return Observable.just(pageable)
    }

    private fun createPageable(link: String?, value: T?): Pageable<T> {
        var next: Int? = null
        var last: Int? = null

        if (link != null && !link.isEmpty())
            link.split(",".toRegex()).forEach {
                val paginationLink = PaginationLink(it)

                when (paginationLink.rel) {
                    RelType.LAST -> last = paginationLink.page
                    RelType.NEXT -> next = paginationLink.page
                }
            }

        return Pageable(value, last, next)
    }
}
