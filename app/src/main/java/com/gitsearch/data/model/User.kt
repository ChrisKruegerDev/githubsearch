package com.gitsearch.data.model

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("avatar_url") val avatarUrl: String? = null
)
