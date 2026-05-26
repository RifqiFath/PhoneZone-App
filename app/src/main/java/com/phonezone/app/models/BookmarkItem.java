package com.phonezone.app.models;

public class BookmarkItem {
    private int articleId;
    private String title;
    private String excerpt;
    private String imageUrl;
    private String date;
    private String categoryName;
    private String authorName;
    private long savedAt;
    private String contentHtml;

    public BookmarkItem() {}

    public BookmarkItem(Article article) {
        this.articleId = article.getId();
        this.title = article.getTitleRendered();
        this.excerpt = article.getExcerptRendered();
        this.imageUrl = article.getFeaturedImageUrl();
        this.date = article.getDate();
        this.categoryName = article.getPrimaryCategoryName();
        this.authorName = article.getAuthorName();
        this.savedAt = System.currentTimeMillis();
    }

    // Getters & Setters
    public int getArticleId() { return articleId; }
    public void setArticleId(int articleId) { this.articleId = articleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public long getSavedAt() { return savedAt; }
    public void setSavedAt(long savedAt) { this.savedAt = savedAt; }

    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
}