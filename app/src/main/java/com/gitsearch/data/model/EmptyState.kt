package com.gitsearch.data.model

data class EmptyState(
        val title: String,
        val message: String,
        val iconRes: Int,
        val buttonName: String? = null,
        val action: (() -> Unit)? = null
)