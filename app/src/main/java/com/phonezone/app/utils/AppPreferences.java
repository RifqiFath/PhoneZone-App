package com.phonezone.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phonezone.app.models.BookmarkItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppPreferences {

    private static final String PREF_NAME = "PhoneZonePrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_BOOKMARKS = "bookmarks";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    private static AppPreferences instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private AppPreferences(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    // ===== AUTH =====
    public void setLoggedIn(boolean loggedIn, String username) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
                .putString(KEY_USERNAME, username)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "Guest");
    }

    public void logout() {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .putString(KEY_USERNAME, "")
                .apply();
    }

    // ===== SETTINGS =====
    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setNotifications(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    // ===== FIRST LAUNCH =====
    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean isFirst) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply();
    }

    // ===== BOOKMARKS =====
    public List<BookmarkItem> getBookmarks() {
        String json = prefs.getString(KEY_BOOKMARKS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<BookmarkItem>>() {}.getType();
        List<BookmarkItem> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void saveBookmarks(List<BookmarkItem> bookmarks) {
        String json = gson.toJson(bookmarks);
        prefs.edit().putString(KEY_BOOKMARKS, json).apply();
    }

    public boolean isBookmarked(int articleId) {
        List<BookmarkItem> bookmarks = getBookmarks();
        for (BookmarkItem item : bookmarks) {
            if (item.getArticleId() == articleId) return true;
        }
        return false;
    }

    public void addBookmark(BookmarkItem item) {
        List<BookmarkItem> bookmarks = getBookmarks();
        // avoid duplicates
        for (BookmarkItem b : bookmarks) {
            if (b.getArticleId() == item.getArticleId()) return;
        }
        bookmarks.add(0, item);
        saveBookmarks(bookmarks);
    }

    public void removeBookmark(int articleId) {
        List<BookmarkItem> bookmarks = getBookmarks();
        bookmarks.removeIf(item -> item.getArticleId() == articleId);
        saveBookmarks(bookmarks);
    }

    public void clearBookmarks() {
        prefs.edit().remove(KEY_BOOKMARKS).apply();
    }

    public BookmarkItem getBookmarkById(int articleId) {
        for (BookmarkItem item : getBookmarks()) {
            if (item.getArticleId() == articleId) return item;
        }
        return null;
    }
}