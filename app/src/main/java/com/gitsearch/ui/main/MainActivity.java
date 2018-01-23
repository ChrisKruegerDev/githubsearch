package com.gitsearch.ui.main;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.gitsearch.R;
import com.gitsearch.data.remote.GitHub;
import com.gitsearch.ui.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

  @BindView(R.id.search_view)
  SearchView searchView;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(android.support.v7.appcompat.R.id.search_close_btn)
  ImageView closeButton;

  @Inject
  GitHub gitHub;

  private SearchPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(toolbar);

    String query;
    if (savedInstanceState == null) {
      query = getIntent().getStringExtra(SearchManager.QUERY);
    }
    else {
      query = savedInstanceState.getString(SearchManager.QUERY);
    }

    SearchResultFragment repositoryFragment = (SearchResultFragment) getFragmentManager().findFragmentById(R.id.content_frame);
    if (repositoryFragment == null) {
      repositoryFragment = new SearchResultFragment();
      getFragmentManager().beginTransaction()
        .replace(R.id.content_frame, repositoryFragment)
        .commit();
    }

    presenter = new SearchPresenter(gitHub, repositoryFragment, query);

    setupSearchView();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.activity_main;
  }

  @Override
  protected boolean useInject() {
    return true;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (presenter != null) {
      outState.putString(SearchManager.QUERY, presenter.getQuery());
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent.hasExtra(SearchManager.QUERY)) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      if (!TextUtils.isEmpty(query)) {
        presenter.startSearch(query);
        searchView.setQuery(query, false);
      }
    }
  }

  private void setupSearchView() {
    SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
    if (searchManager == null) {
      Timber.w("searchManager == null");
    }
    else {
      SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
      searchView.setSearchableInfo(searchableInfo);
    }

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        presenter.startSearch(s);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        checkCloseButton(s);
        return true;
      }
    });

    searchView.setOnSearchClickListener(v -> checkCloseButton(searchView.getQuery()));

    searchView.setOnCloseListener(() -> {
      searchView.setQuery("", false);
      return true;
    });
  }

  private void checkCloseButton(CharSequence query) {
    closeButton.setVisibility(TextUtils.isEmpty(query) ? View.GONE : View.VISIBLE);
  }

}
