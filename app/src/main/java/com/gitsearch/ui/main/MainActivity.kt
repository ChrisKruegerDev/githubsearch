package com.gitsearch.ui.main

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.View
import android.widget.Toast
import com.gitsearch.R
import com.gitsearch.ui.BaseActivity
import kotlinx.android.synthetic.main.abc_search_view.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setupSearchView()
    }

    private fun setupSearchView() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        search_view.setSearchableInfo(searchableInfo)

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                search_view.clearFocus()
                viewModel.startSearch(s)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                checkCloseButton(s)
                return true
            }
        })

        search_view.setOnSearchClickListener { checkCloseButton(search_view.query) }
        search_view.setOnCloseListener {
            search_view.setQuery("", false)
            viewModel.startSearch("")
            true
        }
    }

    private fun checkCloseButton(query: CharSequence) {
        search_close_btn.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
    }

}
