package com.gitsearch.ui.main;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gitsearch.data.model.Pageable;
import com.gitsearch.data.model.PaginationLink;
import com.gitsearch.data.model.RelType;
import com.gitsearch.data.model.Repository;
import com.gitsearch.data.remote.GitHub;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;
import timber.log.Timber;


public class SearchPresenter implements SearchContract.Presenter {

  public static final int START_PAGE = 1;
  public static final int NO_PAGE = -1;

  @NonNull
  private final GitHub gitHub;

  @NonNull
  private final SearchContract.View view;

  private String query;
  private int page = START_PAGE;
  private int lastPage = Integer.MAX_VALUE;

  private boolean isLoading = false;

  private final CompositeDisposable compositeDisposable;

  SearchPresenter(@NonNull GitHub gitHub, @NonNull SearchContract.View view, String query) {
    this.gitHub = gitHub;
    this.view = view;
    this.query = query;
    compositeDisposable = new CompositeDisposable();
    view.setPresenter(this);
  }

  @Override
  public void unsubscribe() {
    compositeDisposable.clear();
  }

  @Override
  public String getQuery() {
    return query;
  }

  @Override
  public void startSearch(String query) {
    this.query = query;
    search(query);
  }

  @Override
  public void refreshSearch() {
    search(query);
  }

  @Override
  public boolean isLoading() {
    return isLoading;
  }

  @Override
  public void loadNextPage() {
    searchRepositories(query, ++page);
  }

  @Override
  public boolean hasNextPage() {
    return lastPage > page;
  }

  private void search(String query) {
    if (!TextUtils.isEmpty(query)) {
      view.clearRepositories();
      searchRepositories(query, START_PAGE);
    }
    else {
      view.showEmptyQuery();
    }
  }

  private void searchRepositories(String query, int page) {
    boolean initialSearch = page == START_PAGE;
    if (initialSearch) {
      lastPage = Integer.MAX_VALUE;
    }

    if (page > lastPage || lastPage <= 0) {
      view.showNoRepositories();
      return;
    }

    this.page = page;

    view.showProgress(initialSearch);
    isLoading = true;

    Disposable disposable = gitHub.search()
      .repositories(query, page)
      .compose(gitHub.applyRateLimit())
      .subscribeOn(Schedulers.io())
      .flatMap(new PaginationMapper<>())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete(() -> isLoading = false)
      .subscribe(result -> {
        lastPage = result.last;
        if (view.isActive()) {
          progressRepositories(result.value.items, page);
        }
        isLoading = false;
      }, t -> {
        Timber.e(t);
        if (view.isActive()) {
          view.showLoadingRepositoriesError();
        }
        isLoading = false;
      });

    compositeDisposable.add(disposable);
  }

  private void progressRepositories(List<Repository> repositories, int page) {
    if (page == START_PAGE) {
      view.showRepositories(repositories);
    }
    else {
      view.showFurtherRepositories(repositories);
    }
  }

  private static class PaginationMapper<T> implements Function<Result<T>, ObservableSource<? extends Pageable<T>>> {
    @Override
    public ObservableSource<? extends Pageable<T>> apply(Result<T> result) {
      if (result.isError()) {
        Throwable throwable = result.error();
        return throwable == null
          ? Observable.error(new RuntimeException("error while request"))
          : Observable.error(throwable);
      }

      Response<T> response = result.response();
      if (response == null) {
        return Observable.error(new IllegalStateException("response == null"));
      }

      Headers headers = response.headers();
      String link = headers == null ? null : headers.get(GitHub.HEADER_LINK);

      int page = getLastPage(link);
      T value = response.body();

      return Observable.just(new Pageable<>(value, page));
    }

    private int getLastPage(String link) {
      if (!TextUtils.isEmpty(link)) {
        String[] linkParts = link.split(",");
        for (String linkPart : linkParts) {
          PaginationLink paginationLink = new PaginationLink(linkPart);
          if (RelType.LAST.equals(paginationLink.getRel())) {
            return paginationLink.getPage();
          }
        }
      }

      return NO_PAGE;
    }
  }

}
