package com.phonezone.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.phonezone.app.R;
import com.phonezone.app.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // Rotating colors for category cards
    private static final int[] CARD_COLORS = {
            0xFF1565C0, 0xFFFF6F00, 0xFF2E7D32, 0xFFC62828,
            0xFF6A1B9A, 0xFF00838F, 0xFFAD1457, 0xFF0277BD,
            0xFF558B2F, 0xFFEF6C00, 0xFF37474F, 0xFF283593
    };

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.tvName.setText(category.getName());
        holder.tvCount.setText(category.getCount() + " artikel");

        // Apply rotating color
        int color = CARD_COLORS[position % CARD_COLORS.length];
        holder.cardView.setCardBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_category);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvCount = itemView.findViewById(R.id.tv_article_count);
        }
    }
}
