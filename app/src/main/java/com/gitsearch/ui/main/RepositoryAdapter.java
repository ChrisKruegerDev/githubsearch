package com.gitsearch.ui.main;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.gitsearch.R;
import com.gitsearch.data.model.Repository;
import com.gitsearch.glide.GlideApp;
import com.gitsearch.glide.GlideRequest;
import com.gitsearch.glide.GlideRequests;
import com.gitsearch.glide.GlideUtils;
import com.gitsearch.widgets.ListRecyclerViewAdapter;

import java.util.Locale;

class RepositoryAdapter extends ListRecyclerViewAdapter<Repository, RepositoryAdapter.RepositoryViewHolder> {

    @NonNull
    private final GlideRequest<Drawable> fullRequest;

    @NonNull
    private final GlideRequest<Drawable> thumbRequest;

    @NonNull
    private final GlideRequest<Drawable> preloadRequest;

    @NonNull
    private final GlideRequests requests;

    @NonNull
    private final ViewPreloadSizeProvider<Repository> preloadSizeProvider;

    RepositoryAdapter(@NonNull Fragment fragment, @NonNull ViewPreloadSizeProvider<Repository> preloadSizeProvider) {
        this.preloadSizeProvider = preloadSizeProvider;

        requests = GlideApp.with(fragment);
        fullRequest = GlideUtils.getAvatar(fragment.getActivity(), requests);
        thumbRequest = GlideUtils.getAvatarPreload(requests);
        preloadRequest = thumbRequest.clone().priority(Priority.HIGH);
    }

    @Override
    public void onViewRecycled(RepositoryViewHolder holder) {
        super.onViewRecycled(holder);
        requests.clear(holder.avatar);
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_repository, parent, false);
        RepositoryViewHolder holder = new RepositoryViewHolder(item);
        preloadSizeProvider.setView(holder.avatar);
        return holder;
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
        Repository item = getItem(position);
        if (item != null) {
            String avatarUrl = item.owner.avatarUrl;
            fullRequest.thumbnail(thumbRequest.load(avatarUrl))
                    .load(avatarUrl)
                    .into(holder.avatar);
            holder.title.setText(item.fullName);
            String score = String.format(Locale.getDefault(), "%.4f", item.score);
            holder.subtitle.setText(score);
        }
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Repository item) {
        return preloadRequest.load(item.owner.avatarUrl);
    }

    @Override
    public long getItemId(int position) {
        Repository item = getItem(position);
        return item == null ? super.getItemId(position) : item.id;
    }

    class RepositoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        ImageView avatar;

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.subtitle)
        TextView subtitle;

        RepositoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
