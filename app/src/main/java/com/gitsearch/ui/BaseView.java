package com.gitsearch.ui;

import android.support.annotation.NonNull;

public interface BaseView<T extends BasePresenter> {
  void setPresenter(@NonNull T presenter);
}
