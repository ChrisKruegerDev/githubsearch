package com.gitsearch.data.model

import android.net.Uri
import android.text.TextUtils
import com.gitsearch.data.remote.GitHub

const val PARAMETER_PAGE = "page"

data class PaginationLink(private val link: String) {

    val rel: String
    val page: Int

    init {
        val value = link.trim { it <= ' ' }
                .replace("<", "")
                .replace(">", "")
                .replace("\"", "")

        val url = value.split(";".toRegex())[0]
        val uri = Uri.parse(url)
        val pageParameter = uri.getQueryParameter(PARAMETER_PAGE)

        page = if (pageParameter.isNullOrBlank()) 0 else pageParameter.toInt()
        rel = value.split("rel=".toRegex())[1]
    }

}
