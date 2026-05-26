package com.phonezone.app.api;

import com.phonezone.app.models.Article;
import com.phonezone.app.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordPressApiService {

    // ===== POSTS / ARTICLES =====

    /**
     * Get all posts with embedded data (author, featured media, categories)
     */
    @GET("wp/v2/posts")
    Call<List<Article>> getPosts(
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("_embed") String embed,
            @Query("status") String status
    );

    /**
     * Get posts by category
     */
    @GET("wp/v2/posts")
    Call<List<Article>> getPostsByCategory(
            @Query("categories") int categoryId,
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("_embed") String embed
    );

    /**
     * Search posts
     */
    @GET("wp/v2/posts")
    Call<List<Article>> searchPosts(
            @Query("search") String query,
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("_embed") String embed
    );

    /**
     * Get single post by ID
     */
    @GET("wp/v2/posts/{id}")
    Call<Article> getPostById(
            @Path("id") int id,
            @Query("_embed") String embed
    );

    /**
     * Get posts by multiple IDs (for bookmark detail)
     */
    @GET("wp/v2/posts")
    Call<List<Article>> getPostsByIds(
            @Query("include") String ids,
            @Query("_embed") String embed
    );

    // ===== CATEGORIES =====

    /**
     * Get all categories
     */
    @GET("wp/v2/categories")
    Call<List<Category>> getCategories(
            @Query("per_page") int perPage,
            @Query("hide_empty") boolean hideEmpty
    );

    /**
     * Get category by ID
     */
    @GET("wp/v2/categories/{id}")
    Call<Category> getCategoryById(
            @Path("id") int id
    );

    // ===== FEATURED/LATEST =====

    /**
     * Get featured (sticky) posts
     */
    @GET("wp/v2/posts")
    Call<List<Article>> getFeaturedPosts(
            @Query("sticky") boolean sticky,
            @Query("per_page") int perPage,
            @Query("_embed") String embed
    );

    /**
     * Get latest posts (shortcut)
     */
    @GET("wp/v2/posts?per_page=10&_embed=1")
    Call<List<Article>> getLatestPosts();

    /**
     * Get posts ordered by views / comments
     */
    @GET("wp/v2/posts")
    Call<List<Article>> getPopularPosts(
            @Query("orderby") String orderby,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("_embed") String embed
    );
}
