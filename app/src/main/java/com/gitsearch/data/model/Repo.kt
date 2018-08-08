package com.gitsearch.data.model

import com.google.gson.annotations.SerializedName

data class Repo(
        val id: Long = 0,
        @SerializedName("full_name") val fullName: String? = null,
        val owner: User? = null,
        val score: Double = 0.0
)





