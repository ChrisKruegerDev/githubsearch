package com.gitsearch.data.model

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

data class NetworkState private constructor(
        val status: Status,
        val throwable: Throwable? = null
) {

    companion object {

        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)

        fun error(throwable: Throwable?) = NetworkState(Status.FAILED, throwable)

    }

}