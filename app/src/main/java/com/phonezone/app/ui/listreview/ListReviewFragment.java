package com.phonezone.app.ui.listreview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.phonezone.app.R;
import com.phonezone.app.adapters.ArticleGridAdapter;
import com.phonezone.app.models.Article;
import com.phonezone.app.network.ApiClient;
import com.phonezone.app.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ListReviewFragment - displays articles in a 2-column grid view (review style)
 * Used as a tab inside CategoryFragment (TabLayout + ViewPager2)
 */
public class ListReviewFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";

    private RecyclerView rvGrid;
    private ArticleGridAdapter gridAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmer;
    private View layoutEmpty, layoutError;
    private List<Article> articles = new ArrayList<>();
    private int categoryId;

    public static ListReviewFragment newInstance(int categoryId) {
        ListReviewFragment fragment = new ListReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        loadArticles();
    }

    private void initViews(View view) {
        rvGrid = view.findViewById(R.id.rv_grid);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        shimmer = view.findViewById(R.id.shimmer_grid);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutError = view.findViewById(R.id.layout_error);

        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            articles.clear();
            if (gridAdapter != null) gridAdapter.notifyDataSetChanged();
            loadArticles();
        });

        view.findViewById(R.id.btn_retry).setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            loadArticles();
        });
    }

    private void setupRecyclerView() {
        // GridView - 2 columns
        gridAdapter = new ArticleGridAdapter(requireContext(), articles, article -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_ID, article.getId());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, article.getTitleRendered());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_IMAGE, article.getFeaturedImageUrl());
            startActivity(intent);
        });
        rvGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvGrid.setAdapter(gridAdapter);
    }

    private void loadArticles() {
        shimmer.startShimmer();
        shimmer.setVisibility(View.VISIBLE);
        rvGrid.setVisibility(View.GONE);

        Call<List<Article>> call;
        if (categoryId == 0) {
            call = ApiClient.getInstance().getApiService().getPosts(1, 20, "1", "publish");
        } else {
            call = ApiClient.getInstance().getApiService().getPostsByCategory(categoryId, 1, 20, "1");
        }

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                swipeRefresh.setRefreshing(false);
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                rvGrid.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    articles.addAll(response.body());
                    gridAdapter.notifyDataSetChanged();

                    if (articles.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvGrid.setVisibility(View.GONE);
                    }
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                layoutError.setVisibility(View.VISIBLE);
            }
        });
    }
}
