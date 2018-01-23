package com.gitsearch.widgets;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

/**
 * User: Christian Krueger
 * Date: 09.08.2017
 *
 * @since 0.2
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

  private final int thresholdCount;
  private final int orientation;

  public EndlessScrollListener(int thresholdCount, int orientation) {
    this.thresholdCount = thresholdCount;
    this.orientation = orientation;
  }

  @Override
  public void onScrolled(RecyclerView rv, int dx, int dy) {
    if ((dy <= 0 && orientation == LinearLayout.VERTICAL)
      || (dx <= 0 && orientation == LinearLayout.HORIZONTAL)
      || !isEnable()) {
      return;
    }

    if(isLoading()) {
      return;
    }

    LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
    int firstVisible = lm.findFirstVisibleItemPosition();
    int visibleCount = Math.abs(firstVisible - lm.findLastVisibleItemPosition());
    int itemCount = rv.getAdapter().getItemCount();

    if (itemCount - visibleCount <= firstVisible + thresholdCount) {
      onExecute();
    }
  }

  public abstract boolean isEnable();

  public abstract boolean isLoading();

  public abstract void onExecute();

}
