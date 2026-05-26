package com.phonezone.app.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.phonezone.app.R;
import com.phonezone.app.adapters.ArticleAdapter;
import com.phonezone.app.adapters.CategoryAdapter;
import com.phonezone.app.models.Article;
import com.phonezone.app.models.Category;
import com.phonezone.app.network.ApiClient;
import com.phonezone.app.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private RecyclerView rvCategories, rvArticles;
    private CategoryAdapter categoryAdapter;
    private ArticleAdapter articleAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmerCategories, shimmerArticles;
    private TextView tvCategoryTitle, tvArticleCount;
    private View layoutEmpty, layoutError;

    private List<Category> categories = new ArrayList<>();
    private List<Article> articles = new ArrayList<>();
    private int selectedCategoryId = 0; // 0 = All

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerViews();
        setupSwipeRefresh();
        loadCategories();
        loadArticles(0);
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvArticles = view.findViewById(R.id.rv_articles);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        shimmerCategories = view.findViewById(R.id.shimmer_categories);
        shimmerArticles = view.findViewById(R.id.shimmer_articles);
        tvCategoryTitle = view.findViewById(R.id.tv_category_title);
        tvArticleCount = view.findViewById(R.id.tv_article_count);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutError = view.findViewById(R.id.layout_error);

        view.findViewById(R.id.btn_retry).setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            loadCategories();
            loadArticles(selectedCategoryId);
        });
    }

    private void setupRecyclerViews() {
        // Grid view for categories
        categoryAdapter = new CategoryAdapter(requireContext(), categories, category -> {
            selectedCategoryId = category.getId();
            tvCategoryTitle.setText(category.getName());
            articles.clear();
            articleAdapter.notifyDataSetChanged();
            loadArticles(category.getId());
        });
        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvCategories.setAdapter(categoryAdapter);

        // List view for articles
        articleAdapter = new ArticleAdapter(requireContext(), articles, article -> {
            openDetail(article);
        });
        rvArticles.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvArticles.setAdapter(articleAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            loadCategories();
            loadArticles(selectedCategoryId);
        });
    }

    private void loadCategories() {
        shimmerCategories.startShimmer();
        shimmerCategories.setVisibility(View.VISIBLE);

        ApiClient.getInstance().getApiService()
                .getCategories(50, true)
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        shimmerCategories.stopShimmer();
                        shimmerCategories.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            categories.clear();
                            categories.addAll(response.body());
                            categoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        shimmerCategories.stopShimmer();
                        shimmerCategories.setVisibility(View.GONE);
                    }
                });
    }

    private void loadArticles(int categoryId) {
        shimmerArticles.startShimmer();
        shimmerArticles.setVisibility(View.VISIBLE);
        rvArticles.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        Call<List<Article>> call;
        if (categoryId == 0) {
            call = ApiClient.getInstance().getApiService().getPosts(1, 100, "1", "publish");
        } else {
            call = ApiClient.getInstance().getApiService().getPostsByCategory(categoryId, 1, 100, "1");
        }

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                swipeRefresh.setRefreshing(false);
                shimmerArticles.stopShimmer();
                shimmerArticles.setVisibility(View.GONE);
                rvArticles.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    articles.clear();
                    articles.addAll(response.body());
                    articleAdapter.notifyDataSetChanged();
                    tvArticleCount.setText(articles.size() + " artikel");

                    if (articles.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvArticles.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                shimmerArticles.stopShimmer();
                shimmerArticles.setVisibility(View.GONE);
                layoutError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openDetail(Article article) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_ARTICLE_ID, article.getId());
        intent.putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, article.getTitleRendered());
        intent.putExtra(DetailActivity.EXTRA_ARTICLE_IMAGE, article.getFeaturedImageUrl());
        startActivity(intent);
    }
}