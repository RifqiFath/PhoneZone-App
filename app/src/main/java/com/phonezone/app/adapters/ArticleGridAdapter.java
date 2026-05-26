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
import com.phonezone.app.R;
import com.phonezone.app.models.Article;
import com.phonezone.app.utils.DateUtils;
import com.phonezone.app.utils.HtmlUtils;

import java.util.List;

public class ArticleGridAdapter extends RecyclerView.Adapter<ArticleGridAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    private final Context context;
    private final List<Article> articles;
    private final OnItemClickListener listener;

    public ArticleGridAdapter(Context context, List<Article> articles, OnItemClickListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);

        holder.tvTitle.setText(HtmlUtils.decodeTitle(article.getTitleRendered()));
        holder.tvDate.setText(DateUtils.getRelativeTime(article.getDate()));
        holder.tvCategory.setText(article.getPrimaryCategoryName());

        String imageUrl = article.getFeaturedImageUrl();
        if (!imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(article);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDate, tvCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
}
