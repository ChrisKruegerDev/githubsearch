package com.gitsearch.data.model

data class Pageable<T>(
        val value: T?,
        val last: Int?,
        val next: Int?
)
