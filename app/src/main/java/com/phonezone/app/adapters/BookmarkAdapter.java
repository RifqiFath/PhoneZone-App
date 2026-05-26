package com.phonezone.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.phonezone.app.R;
import com.phonezone.app.models.BookmarkItem;
import com.phonezone.app.utils.DateUtils;
import com.phonezone.app.utils.HtmlUtils;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(BookmarkItem item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(BookmarkItem item);
    }

    private final Context context;
    private final List<BookmarkItem> bookmarks;
    private final OnItemClickListener clickListener;
    private final OnDeleteClickListener deleteListener;

    public BookmarkAdapter(Context context, List<BookmarkItem> bookmarks,
                           OnItemClickListener clickListener,
                           OnDeleteClickListener deleteListener) {
        this.context = context;
        this.bookmarks = bookmarks;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkItem item = bookmarks.get(position);

        holder.tvTitle.setText(HtmlUtils.decodeTitle(item.getTitle()));
        holder.tvExcerpt.setText(HtmlUtils.cleanExcerpt(item.getExcerpt()));
        holder.tvDate.setText(DateUtils.formatDate(item.getDate()));
        holder.tvCategory.setText(item.getCategoryName());
        holder.tvAuthor.setText(item.getAuthorName());

        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvExcerpt, tvDate, tvCategory, tvAuthor;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvExcerpt = itemView.findViewById(R.id.tv_excerpt);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
