package com.phonezone.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.phonezone.app.R;
import com.phonezone.app.adapters.ArticleAdapter;
import com.phonezone.app.adapters.FeaturedPagerAdapter;
import com.phonezone.app.models.Article;
import com.phonezone.app.network.ApiClient;
import com.phonezone.app.ui.detail.DetailActivity;
import com.phonezone.app.utils.AppPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvLatest, rvPopular;
    private ArticleAdapter latestAdapter, popularAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmerLatest, shimmerFeatured;
    private View layoutError;
    private TextView tvGreeting;
    private androidx.viewpager2.widget.ViewPager2 vpFeatured;
    private FeaturedPagerAdapter featuredAdapter;

    private List<Article> latestArticles = new ArrayList<>();
    private List<Article> featuredArticles = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        android.util.Log.d("DEBUG_HOME", "onViewCreated dipanggil");

        initViews(view);
        setupRecyclerViews();
        setupSwipeRefresh();
        loadGreeting();
        loadFeaturedArticles();
        loadLatestArticles(true);
    }

    private void initViews(View view) {
        rvLatest = view.findViewById(R.id.rv_latest);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        shimmerLatest = view.findViewById(R.id.shimmer_latest);
        shimmerFeatured = view.findViewById(R.id.shimmer_featured);
        layoutError = view.findViewById(R.id.layout_error);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        vpFeatured = view.findViewById(R.id.vp_featured);

        view.findViewById(R.id.btn_retry).setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            loadFeaturedArticles();
            loadLatestArticles(true);
        });
    }

    private void loadGreeting() {
        AppPreferences prefs = AppPreferences.getInstance(requireContext());
        String username = prefs.getUsername();
        String greeting = username.equals("Guest") ? "Halo, Selamat Datang! 👋"
                : "Halo, " + username + "! 👋";
        tvGreeting.setText(greeting);
    }

    private void setupRecyclerViews() {
        latestAdapter = new ArticleAdapter(requireContext(), latestArticles, article -> {
            openDetail(article);
        });
        rvLatest.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLatest.setAdapter(latestAdapter);

        // Infinite scroll
        rvLatest.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !isLoading && hasMorePages) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (lm != null) {
                        int visible = lm.getChildCount();
                        int total = lm.getItemCount();
                        int firstVisible = lm.findFirstVisibleItemPosition();
                        if ((visible + firstVisible) >= total - 2) {
                            loadLatestArticles(false);
                        }
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> {
            currentPage = 1;
            latestArticles.clear();
            latestAdapter.notifyDataSetChanged();
            loadFeaturedArticles();
            loadLatestArticles(true);
        });
    }

    private void loadFeaturedArticles() {
        shimmerFeatured.startShimmer();
        shimmerFeatured.setVisibility(View.VISIBLE);

        ApiClient.getInstance().getApiService()
                .getPosts(1, 5, "1", "publish")
                .enqueue(new Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        shimmerFeatured.stopShimmer();
                        shimmerFeatured.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            featuredArticles.clear();
                            featuredArticles.addAll(response.body());
                            setupFeaturedPager();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        shimmerFeatured.stopShimmer();
                        shimmerFeatured.setVisibility(View.GONE);
                    }
                });
    }

    private void setupFeaturedPager() {
        if (!isAdded() || getContext() == null) return;
        featuredAdapter = new FeaturedPagerAdapter(requireContext(), featuredArticles, article -> {
            openDetail(article);
        });
        vpFeatured.setAdapter(featuredAdapter);
        vpFeatured.setOffscreenPageLimit(3);

        // Auto-scroll featured
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vpFeatured != null && featuredAdapter != null && featuredArticles.size() > 1) {
                    int next = (vpFeatured.getCurrentItem() + 1) % featuredArticles.size();
                    vpFeatured.setCurrentItem(next, true);
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this, 4000);
                }
            }
        }, 4000);
    }

    private void loadLatestArticles(boolean isRefresh) {
        if (isLoading) return;
        isLoading = true;

        if (isRefresh) {
            shimmerLatest.startShimmer();
            shimmerLatest.setVisibility(View.VISIBLE);
            rvLatest.setVisibility(View.GONE);
            currentPage = 1;
        }

        ApiClient.getInstance().getApiService()
                .getPosts(currentPage, 10, "1", "publish")
                .enqueue(new Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        isLoading = false;
                        swipeRefresh.setRefreshing(false);
                        shimmerLatest.stopShimmer();
                        shimmerLatest.setVisibility(View.GONE);
                        rvLatest.setVisibility(View.VISIBLE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Article> articles = response.body();
                            if (articles.isEmpty()) {
                                hasMorePages = false;
                            } else {
                                latestArticles.addAll(articles);
                                latestAdapter.notifyDataSetChanged();
                                currentPage++;
                                hasMorePages = articles.size() == 10;
                            }
                        } else {
                            showError();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        isLoading = false;
                        swipeRefresh.setRefreshing(false);
                        shimmerLatest.stopShimmer();
                        shimmerLatest.setVisibility(View.GONE);
                        android.util.Log.e("DEBUG_HOME", "API GAGAL: " + t.getMessage());
                        showError();
                    }
                });
    }

    private void showError() {
        if (latestArticles.isEmpty()) {
            layoutError.setVisibility(View.VISIBLE);
            rvLatest.setVisibility(View.GONE);
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDetail(Article article) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_ARTICLE_ID, article.getId());
        intent.putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, article.getTitleRendered());
        intent.putExtra(DetailActivity.EXTRA_ARTICLE_IMAGE, article.getFeaturedImageUrl());
        startActivity(intent);
    }
}
