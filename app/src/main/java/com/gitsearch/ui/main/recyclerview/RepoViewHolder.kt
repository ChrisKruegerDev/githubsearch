package com.gitsearch.ui.main.recyclerview

import android.view.ViewGroup
import android.widget.ImageView
import com.gitsearch.R
import com.gitsearch.data.model.Repo
import kotlinx.android.synthetic.main.list_item_repository.*
import java.util.*

class RepoViewHolder(
        parent: ViewGroup
) : LayoutViewHolder(parent, R.layout.list_item_repository), ImageViewHolder {

    override val image: ImageView get() = avatar

    fun bindTo(repo: Repo?) {
        title.text = repo?.fullName
        subtitle.text = String.format(Locale.getDefault(), "%.4f", repo?.score)
    }


}
