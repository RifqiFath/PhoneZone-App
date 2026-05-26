package com.phonezone.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.chip.Chip;
import com.phonezone.app.R;
import com.phonezone.app.models.Article;
import com.phonezone.app.utils.DateUtils;
import com.phonezone.app.utils.HtmlUtils;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    private final Context context;
    private final List<Article> articles;
    private final OnArticleClickListener listener;

    public ArticleAdapter(Context context, List<Article> articles, OnArticleClickListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);

        holder.tvTitle.setText(HtmlUtils.decodeTitle(article.getTitleRendered()));
        holder.tvExcerpt.setText(HtmlUtils.cleanExcerpt(article.getExcerptRendered()));
        holder.tvAuthor.setText(article.getAuthorName());
        holder.tvDate.setText(DateUtils.getRelativeTime(article.getDate()));
        holder.tvCategory.setText(article.getPrimaryCategoryName());

        // Load image
        String imageUrl = article.getFeaturedImageUrl();
        if (!imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onArticleClick(article);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvExcerpt, tvAuthor, tvDate, tvCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvExcerpt = itemView.findViewById(R.id.tv_excerpt);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
}
