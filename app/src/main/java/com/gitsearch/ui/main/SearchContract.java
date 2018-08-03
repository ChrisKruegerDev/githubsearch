package com.gitsearch.ui.main;

import android.support.annotation.Nullable;
import com.gitsearch.data.model.Repository;
import com.gitsearch.ui.BasePresenter;
import com.gitsearch.ui.BaseView;

import java.util.List;

public interface SearchContract {

    interface View extends BaseView<Presenter> {

        void showEmptyQuery();

        void showNoRepositories();

        void showLoadingRepositoriesError();

        void showProgress(boolean initialSearch);

        void hideProgress();

        void clearRepositories();

        void showRepositories(List<Repository> results);

        void showFurtherRepositories(List<Repository> results);

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void startSearch(String query);

        @Nullable
        String getQuery();

        void refreshSearch();

        void loadNextPage();

        boolean isLoading();

        boolean hasNextPage();

    }
}
