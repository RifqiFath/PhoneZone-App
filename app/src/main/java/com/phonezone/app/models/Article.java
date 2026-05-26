package com.phonezone.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Article {

    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private String date;

    @SerializedName("modified")
    private String modified;

    @SerializedName("slug")
    private String slug;

    @SerializedName("status")
    private String status;

    @SerializedName("title")
    private Title title;

    @SerializedName("content")
    private Content content;

    @SerializedName("excerpt")
    private Excerpt excerpt;

    @SerializedName("featured_media")
    private int featuredMedia;

    @SerializedName("categories")
    private List<Integer> categories;

    @SerializedName("tags")
    private List<Integer> tags;

    @SerializedName("_embedded")
    private Embedded embedded;

    // Getters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getModified() { return modified; }
    public String getSlug() { return slug; }
    public String getStatus() { return status; }
    public Title getTitle() { return title; }
    public Content getContent() { return content; }
    public Excerpt getExcerpt() { return excerpt; }
    public int getFeaturedMedia() { return featuredMedia; }
    public List<Integer> getCategories() { return categories; }
    public List<Integer> getTags() { return tags; }
    public Embedded getEmbedded() { return embedded; }

    public String getTitleRendered() {
        return title != null ? title.getRendered() : "";
    }

    public String getContentRendered() {
        return content != null ? content.getRendered() : "";
    }

    public String getExcerptRendered() {
        return excerpt != null ? excerpt.getRendered() : "";
    }

    public String getFeaturedImageUrl() {
        if (embedded != null && embedded.getWpFeaturedmedia() != null
                && !embedded.getWpFeaturedmedia().isEmpty()) {
            FeaturedMedia media = embedded.getWpFeaturedmedia().get(0);
            if (media != null && media.getMediaDetails() != null) {
                MediaSizes sizes = media.getMediaDetails().getSizes();
                if (sizes != null) {
                    if (sizes.getMediumLarge() != null) return sizes.getMediumLarge().getSourceUrl();
                    if (sizes.getMedium() != null) return sizes.getMedium().getSourceUrl();
                    if (sizes.getFull() != null) return sizes.getFull().getSourceUrl();
                }
                return media.getSourceUrl();
            }
        }
        return "";
    }

    public String getAuthorName() {
        if (embedded != null && embedded.getAuthor() != null
                && !embedded.getAuthor().isEmpty()) {
            return embedded.getAuthor().get(0).getName();
        }
        return "Admin";
    }

    public String getAuthorAvatar() {
        if (embedded != null && embedded.getAuthor() != null
                && !embedded.getAuthor().isEmpty()) {
            Author author = embedded.getAuthor().get(0);
            if (author.getAvatarUrls() != null) {
                return author.getAvatarUrls().get96();
            }
        }
        return "";
    }

    public String getPrimaryCategoryName() {
        if (embedded != null && embedded.getWpTerm() != null
                && !embedded.getWpTerm().isEmpty()
                && !embedded.getWpTerm().get(0).isEmpty()) {
            return embedded.getWpTerm().get(0).get(0).getName();
        }
        return "Umum";
    }

    // ---- Inner classes ----

    public static class Title {
        @SerializedName("rendered")
        private String rendered;
        public String getRendered() { return rendered; }
    }

    public static class Content {
        @SerializedName("rendered")
        private String rendered;
        @SerializedName("protected")
        private boolean isProtected;
        public String getRendered() { return rendered; }
    }

    public static class Excerpt {
        @SerializedName("rendered")
        private String rendered;
        public String getRendered() { return rendered; }
    }

    public static class Embedded {
        @SerializedName("author")
        private List<Author> author;

        @SerializedName("wp:featuredmedia")
        private List<FeaturedMedia> wpFeaturedmedia;

        @SerializedName("wp:term")
        private List<List<Category>> wpTerm;

        public List<Author> getAuthor() { return author; }
        public List<FeaturedMedia> getWpFeaturedmedia() { return wpFeaturedmedia; }
        public List<List<Category>> getWpTerm() { return wpTerm; }
    }

    public static class Author {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("avatar_urls")
        private AvatarUrls avatarUrls;
        public int getId() { return id; }
        public String getName() { return name; }
        public AvatarUrls getAvatarUrls() { return avatarUrls; }
    }

    public static class AvatarUrls {
        @SerializedName("96")
        private String url96;
        public String get96() { return url96; }
    }

    public static class FeaturedMedia {
        @SerializedName("id")
        private int id;
        @SerializedName("source_url")
        private String sourceUrl;
        @SerializedName("media_details")
        private MediaDetails mediaDetails;
        public int getId() { return id; }
        public String getSourceUrl() { return sourceUrl; }
        public MediaDetails getMediaDetails() { return mediaDetails; }
    }

    public static class MediaDetails {
        @SerializedName("sizes")
        private MediaSizes sizes;
        public MediaSizes getSizes() { return sizes; }
    }

    public static class MediaSizes {
        @SerializedName("medium")
        private SizeInfo medium;
        @SerializedName("medium_large")
        private SizeInfo mediumLarge;
        @SerializedName("full")
        private SizeInfo full;
        public SizeInfo getMedium() { return medium; }
        public SizeInfo getMediumLarge() { return mediumLarge; }
        public SizeInfo getFull() { return full; }
    }

    public static class SizeInfo {
        @SerializedName("source_url")
        private String sourceUrl;
        public String getSourceUrl() { return sourceUrl; }
    }
}
