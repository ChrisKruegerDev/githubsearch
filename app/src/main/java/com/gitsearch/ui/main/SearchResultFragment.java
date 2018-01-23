package com.gitsearch.ui.main;


import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.gitsearch.R;
import com.gitsearch.data.model.Repository;
import com.gitsearch.glide.GlideApp;
import com.gitsearch.ui.BaseFragment;
import com.gitsearch.utils.NetworkUtils;
import com.gitsearch.widgets.EndlessScrollListener;
import com.gitsearch.widgets.ListRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;

public class SearchResultFragment extends BaseFragment implements SearchContract.View, SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.recycler_view)
  public RecyclerView recyclerView;

  @BindView(R.id.swipe_refresh_layout)
  public SwipeRefreshLayout swipeRefreshLayout;

  @BindView(R.id.text_message)
  public TextView textMessage;

  private SearchContract.Presenter presenter;
  private ListRecyclerViewAdapter<Repository, RepositoryAdapter.RepositoryViewHolder> adapter;

  @Override
  public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ViewPreloadSizeProvider<Repository> provider = new ViewPreloadSizeProvider<>();

    int preloadSize = getResources().getInteger(R.integer.repository_preload_size);
    adapter = new RepositoryAdapter(this, provider);
    adapter.setHasStableIds(true);

    recyclerView.setAdapter(adapter);
    recyclerView.setHasFixedSize(true);
    recyclerView.addOnScrollListener(new RecyclerViewPreloader<>(GlideApp.with(this), adapter, provider, preloadSize));
    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
    recyclerView.addOnScrollListener(new PageEndlessScrollListener(preloadSize));

    swipeRefreshLayout.setOnRefreshListener(this);

    onRefresh();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    presenter.unsubscribe();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_search_repository;
  }

  @Override
  public void setPresenter(@NonNull SearchContract.Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void clearRepositories() {
    adapter.setData(null);
  }

  @Override
  public void showRepositories(List<Repository> results) {
    adapter.setData(results);
    showDataViewIfNecessary();
  }

  @Override
  public void showFurtherRepositories(List<Repository> results) {
    adapter.addData(results);
    showDataViewIfNecessary();
  }

  @Override
  public void showNoRepositories() {
    showErrorMessage(R.string.error_no_repositories, 0);
  }

  @Override
  public void showEmptyQuery() {
    showErrorMessage(R.string.start_search, R.drawable.ic_search_48);
  }

  @Override
  public void showLoadingRepositoriesError() {
    if (NetworkUtils.isOnline(getActivity())) {
      showErrorMessage(R.string.error_loading_repositories, R.drawable.ic_sentiment_dissatisfied_48);
    }
    else {
      showErrorMessage(R.string.error_offline, R.drawable.ic_cloud_off_48);
    }
  }

  @Override
  public void showProgress(boolean initialSearch) {
    showLoadingIndicator(true);

    if(initialSearch) {
      textMessage.setVisibility(View.GONE);
      recyclerView.setVisibility(View.GONE);
    }
  }

  @Override
  public void hideProgress() {
    showLoadingIndicator(false);
  }

  @Override
  public void onRefresh() {
    if (presenter != null) {
      presenter.refreshSearch();
    }
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }

  private void showDataViewIfNecessary() {
    if (adapter.getData().isEmpty()) {
      showNoRepositories();
    }
    else {
      hideProgress();
      recyclerView.setVisibility(View.VISIBLE);
      textMessage.setVisibility(View.GONE);
    }
  }

  private void showLoadingIndicator(boolean enable) {
    swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(enable));
  }

  private void showErrorMessage(@StringRes int textResId, @DrawableRes int iconResId) {
    hideProgress();
    recyclerView.setVisibility(View.GONE);

    textMessage.setVisibility(View.VISIBLE);
    textMessage.setText(textResId);
    textMessage.setCompoundDrawablesWithIntrinsicBounds(0, iconResId, 0, 0);
  }

  private class PageEndlessScrollListener extends EndlessScrollListener {

    PageEndlessScrollListener(int preloadSize) {
      super(preloadSize, LinearLayout.VERTICAL);
    }

    @Override
    public boolean isLoading() {
      return presenter.isLoading();
    }

    @Override
    public void onExecute() {
      presenter.loadNextPage();
    }

    @Override
    public boolean isEnable() {
      return presenter != null && presenter.hasNextPage();
    }
  }
}