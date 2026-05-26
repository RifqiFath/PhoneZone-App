package com.phonezone.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.phonezone.app.R;
import com.phonezone.app.adapters.ArticleAdapter;
import com.phonezone.app.models.Article;
import com.phonezone.app.network.ApiClient;
import com.phonezone.app.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageButton btnClear;
    private RecyclerView rvResults;
    private ArticleAdapter searchAdapter;
    private ShimmerFrameLayout shimmerSearch;
    private View layoutEmpty, layoutIdle, layoutError;
    private TextView tvResultCount, tvSearchQuery;

    private List<Article> searchResults = new ArrayList<>();
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500; // ms debounce

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupSearchInput();
        setupRecyclerView();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnClear = view.findViewById(R.id.btn_clear);
        rvResults = view.findViewById(R.id.rv_search_results);
        shimmerSearch = view.findViewById(R.id.shimmer_search);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutIdle = view.findViewById(R.id.layout_idle);
        layoutError = view.findViewById(R.id.layout_error);
        tvResultCount = view.findViewById(R.id.tv_result_count);
        tvSearchQuery = view.findViewById(R.id.tv_search_query);

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            showIdleState();
        });
    }

    private void setupSearchInput() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() < 2) {
                    showIdleState();
                    return;
                }
                // Debounce search
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) performSearch(query);
                return true;
            }
            return false;
        });

        // Focus search input
        etSearch.requestFocus();
    }

    private void setupRecyclerView() {
        searchAdapter = new ArticleAdapter(requireContext(), searchResults, article -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_ID, article.getId());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, article.getTitleRendered());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_IMAGE, article.getFeaturedImageUrl());
            startActivity(intent);
        });
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvResults.setAdapter(searchAdapter);
    }

    private void performSearch(String query) {
        shimmerSearch.startShimmer();
        shimmerSearch.setVisibility(View.VISIBLE);
        rvResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutIdle.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);

        ApiClient.getInstance().getApiService()
                .searchPosts(query, 1, 20, "1")
                .enqueue(new Callback<List<Article>>() {
                    @Override
                    public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                        shimmerSearch.stopShimmer();
                        shimmerSearch.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            searchResults.clear();
                            searchResults.addAll(response.body());
                            searchAdapter.notifyDataSetChanged();

                            tvSearchQuery.setText("\"" + query + "\"");
                            tvResultCount.setText(searchResults.size() + " hasil ditemukan");

                            if (searchResults.isEmpty()) {
                                layoutEmpty.setVisibility(View.VISIBLE);
                                rvResults.setVisibility(View.GONE);
                            } else {
                                layoutEmpty.setVisibility(View.GONE);
                                rvResults.setVisibility(View.VISIBLE);
                            }
                        } else {
                            layoutError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                        shimmerSearch.stopShimmer();
                        shimmerSearch.setVisibility(View.GONE);
                        layoutError.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showIdleState() {
        shimmerSearch.setVisibility(View.GONE);
        rvResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutIdle.setVisibility(View.VISIBLE);
        searchResults.clear();
        if (searchAdapter != null) searchAdapter.notifyDataSetChanged();
    }
}
