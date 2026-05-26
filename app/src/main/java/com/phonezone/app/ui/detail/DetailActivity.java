package com.phonezone.app.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.phonezone.app.R;
import com.phonezone.app.models.Article;
import com.phonezone.app.models.BookmarkItem;
import com.phonezone.app.network.ApiClient;
import com.phonezone.app.utils.AppPreferences;
import com.phonezone.app.utils.DateUtils;
import com.phonezone.app.utils.HtmlUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "article_id";
    public static final String EXTRA_ARTICLE_TITLE = "article_title";
    public static final String EXTRA_ARTICLE_IMAGE = "article_image";

    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageView ivFeaturedImage;
    private TextView tvTitle, tvAuthor, tvDate, tvCategory, tvReadTime;
    private WebView wvContent;
    private Chip chipCategory;
    private LinearLayout layoutAuthor;
    private FloatingActionButton fabBookmark;
    private ShimmerFrameLayout shimmerDetail;
    private View layoutContent, layoutError;
    private NestedScrollView scrollView;

    private int articleId;
    private Article currentArticle;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        prefs = AppPreferences.getInstance(this);
        articleId = getIntent().getIntExtra(EXTRA_ARTICLE_ID, -1);
        String title = getIntent().getStringExtra(EXTRA_ARTICLE_TITLE);
        String imageUrl = getIntent().getStringExtra(EXTRA_ARTICLE_IMAGE);

        initViews();
        setupToolbar();

        // Show initial data while loading
        if (title != null) collapsingToolbar.setTitle(HtmlUtils.decodeTitle(title));
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivFeaturedImage);
        }

        if (articleId != -1) {
            // Cek dulu apakah ada konten offline dari bookmark
            BookmarkItem saved = prefs.getBookmarkById(articleId);
            if (saved != null && saved.getContentHtml() != null && !saved.getContentHtml().isEmpty()) {
                displayOfflineContent(saved);
            } else {
                loadArticleDetail(articleId);
            }
        } else {
            finish();
        }
    }

    private void initViews() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);
        ivFeaturedImage = findViewById(R.id.iv_featured_image);
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvDate = findViewById(R.id.tv_date);
        wvContent = findViewById(R.id.wv_content);
        WebSettings webSettings = wvContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Buka link di browser eksternal, bukan di dalam WebView
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        tvReadTime = findViewById(R.id.tv_read_time);
        chipCategory = findViewById(R.id.chip_category);
        layoutAuthor = findViewById(R.id.layout_author);
        fabBookmark = findViewById(R.id.fab_bookmark);
        shimmerDetail = findViewById(R.id.shimmer_detail);
        layoutContent = findViewById(R.id.layout_content);
        layoutError = findViewById(R.id.layout_error);
        scrollView = findViewById(R.id.scroll_view);

        fabBookmark.setOnClickListener(v -> toggleBookmark());

        findViewById(R.id.btn_retry).setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            loadArticleDetail(articleId);
        });

        findViewById(R.id.btn_share).setOnClickListener(v -> shareArticle());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadArticleDetail(int id) {
        shimmerDetail.startShimmer();
        shimmerDetail.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);

        ApiClient.getInstance().getApiService()
                .getPostById(id, "1")
                .enqueue(new Callback<Article>() {
                    @Override
                    public void onResponse(Call<Article> call, Response<Article> response) {
                        shimmerDetail.stopShimmer();
                        shimmerDetail.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            currentArticle = response.body();
                            displayArticle(currentArticle);
                        } else {
                            layoutError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Article> call, Throwable t) {
                        shimmerDetail.stopShimmer();
                        shimmerDetail.setVisibility(View.GONE);
                        layoutError.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void displayArticle(Article article) {
        layoutContent.setVisibility(View.VISIBLE);

        // Title
        String titleText = HtmlUtils.decodeTitle(article.getTitleRendered());
        tvTitle.setText(titleText);
        collapsingToolbar.setTitle(titleText);

        // Author & Date
        tvAuthor.setText(article.getAuthorName());
        tvDate.setText(DateUtils.formatDateFull(article.getDate()));

        // Category
        chipCategory.setText(article.getPrimaryCategoryName());

        // Read time (estimate: 200 words/min)
        String plainContent = HtmlUtils.stripHtml(article.getContentRendered());
        int wordCount = plainContent.split("\\s+").length;
        int readTime = Math.max(1, wordCount / 200);
        tvReadTime.setText(readTime + " menit baca");

        // Content - render HTML
        boolean isDarkMode = (getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        String textColor = isDarkMode ? "#EEEEEE" : "#212121";
        String bgColor = isDarkMode ? "#121212" : "#FFFFFF";
        String linkColor = isDarkMode ? "#90CAF9" : "#1565C0";

        String cleanContent = HtmlUtils.removeStyleAndScript(article.getContentRendered());
        String htmlPage = "<html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<style>"
                + "body { font-family: sans-serif; font-size: 15px; line-height: 1.7; color: " + textColor + "; background-color: " + bgColor + "; padding: 0; margin: 0; word-wrap: break-word; }"
                + "img { max-width: 100%; height: auto; display: block; margin: 8px 0; }"
                + "a { color: " + linkColor + "; }"
                + "p { margin-bottom: 12px; }"
                + "</style>"
                + "</head><body>"
                + cleanContent
                + "</body></html>";

        wvContent.loadDataWithBaseURL("https://www.phonezone.biz.id/", htmlPage, "text/html", "UTF-8", null);

        // Featured image
        String imageUrl = article.getFeaturedImageUrl();
        if (!imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivFeaturedImage);
        }

        // Bookmark state
        updateBookmarkIcon();
    }

    private void displayOfflineContent(BookmarkItem saved) {
        shimmerDetail.stopShimmer();
        shimmerDetail.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);

        // Title
        String titleText = HtmlUtils.decodeTitle(saved.getTitle());
        tvTitle.setText(titleText);
        collapsingToolbar.setTitle(titleText);

        // Author & Date
        tvAuthor.setText(saved.getAuthorName());
        tvDate.setText(DateUtils.formatDateFull(saved.getDate()));

        // Category
        chipCategory.setText(saved.getCategoryName());

        // Read time
        String plainContent = HtmlUtils.stripHtml(saved.getContentHtml());
        int wordCount = plainContent.split("\\s+").length;
        int readTime = Math.max(1, wordCount / 200);
        tvReadTime.setText(readTime + " menit baca");

        // Render konten HTML offline
        boolean isDarkMode = (getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        String textColor = isDarkMode ? "#EEEEEE" : "#212121";
        String bgColor = isDarkMode ? "#121212" : "#FFFFFF";
        String linkColor = isDarkMode ? "#90CAF9" : "#1565C0";

        String cleanContent = HtmlUtils.removeStyleAndScript(saved.getContentHtml());
        String htmlPage = "<html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<style>"
                + "body { font-family: sans-serif; font-size: 15px; line-height: 1.7; color: " + textColor + "; background-color: " + bgColor + "; padding: 0; margin: 0; word-wrap: break-word; }"
                + "img { max-width: 100%; height: auto; display: block; margin: 8px 0; }"
                + "a { color: " + linkColor + "; }"
                + "p { margin-bottom: 12px; }"
                + "</style>"
                + "</head><body>"
                + cleanContent
                + "</body></html>";

        wvContent.loadDataWithBaseURL("https://www.phonezone.biz.id/", htmlPage, "text/html", "UTF-8", null);

        // Featured image
        if (saved.getImageUrl() != null && !saved.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(saved.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivFeaturedImage);
        }

        // Bookmark icon (pasti sudah di-bookmark)
        fabBookmark.setImageResource(R.drawable.ic_bookmark_filled);
    }

    private void toggleBookmark() {
        if (currentArticle == null) return;

        if (prefs.isBookmarked(currentArticle.getId())) {
            prefs.removeBookmark(currentArticle.getId());
            fabBookmark.setImageResource(R.drawable.ic_bookmark_outline);
            Snackbar.make(coordinatorLayout, getString(R.string.bookmark_removed),
                    Snackbar.LENGTH_SHORT).show();
        } else {
            BookmarkItem item = new BookmarkItem(currentArticle);
            item.setContentHtml(currentArticle.getContentRendered());
            prefs.addBookmark(item);
            fabBookmark.setImageResource(R.drawable.ic_bookmark_filled);
            Snackbar.make(coordinatorLayout, getString(R.string.bookmark_saved),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void updateBookmarkIcon() {
        if (currentArticle != null && prefs.isBookmarked(currentArticle.getId())) {
            fabBookmark.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            fabBookmark.setImageResource(R.drawable.ic_bookmark_outline);
        }
    }

    private void shareArticle() {
        if (currentArticle == null) return;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, HtmlUtils.decodeTitle(currentArticle.getTitleRendered()));
        share.putExtra(Intent.EXTRA_TEXT,
                HtmlUtils.decodeTitle(currentArticle.getTitleRendered())
                        + "\n\n" + ApiClient.BASE_URL.replace("/wp-json/", "")
                        + "/?p=" + currentArticle.getId());
        startActivity(Intent.createChooser(share, "Bagikan artikel via"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}