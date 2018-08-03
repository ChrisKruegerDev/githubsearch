package com.gitsearch.widgets;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;

public abstract class ListRecyclerViewAdapter<T, VH extends ViewHolder> extends RecyclerView.Adapter<VH>
        implements ListPreloader.PreloadModelProvider<T> {

    @NonNull
    private final List<T> data;

    public ListRecyclerViewAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public List<T> getPreloadItems(int position) {
        if (position < 0 || position >= data.size()) {
            return Collections.emptyList();
        } else {
            return data.subList(position, position + 1);
        }
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull T item) {
        return null;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Nullable
    public T getItem(int index) {
        if (index < 0 || index >= data.size()) {
            Timber.e("invalid index: %d", index);
            return null;
        }
        return data.get(index);
    }

    @NonNull
    public List<T> getData() {
        return data;
    }

    public final void setData(@Nullable List<? extends T> newData) {
        data.clear();
        addAll(newData);

        notifyDataSetChanged();
    }

    public final void addData(@Nullable List<? extends T> furtherData) {
        if (furtherData == null || furtherData.isEmpty()) {
            Timber.w("add empty further data");
            return;
        }

        int fromPosition = data.size();
        int itemCount = addAll(furtherData);
        if (itemCount > 0) {
            notifyItemRangeInserted(fromPosition, itemCount);
        }
    }

    private int addAll(@Nullable List<? extends T> newData) {
        if (newData == null) {
            return 0;
        }

        return data.addAll(newData) ? newData.size() : 0;
    }

}
