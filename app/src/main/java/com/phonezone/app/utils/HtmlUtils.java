package com.phonezone.app.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import org.jsoup.Jsoup;

public class HtmlUtils {

    /**
     * Remove <style> and <script> blocks from HTML content
     */
    public static String removeStyleAndScript(String html) {
        if (html == null || html.isEmpty()) return "";
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        doc.select("style, script").remove();
        return doc.body().html();
    }

    /**
     * Strip all HTML tags and return plain text
     */
    public static String stripHtml(String html) {
        if (html == null || html.isEmpty()) return "";
        return Jsoup.parse(html).text();
    }

    /**
     * Strip HTML but keep limited formatting via Html.fromHtml
     */
    public static Spanned fromHtml(String html) {
        if (html == null || html.isEmpty()) return Html.fromHtml("", Html.FROM_HTML_MODE_LEGACY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(html);
        }
    }

    /**
     * Truncate text to given character limit with ellipsis
     */
    public static String truncate(String text, int maxChars) {
        if (text == null) return "";
        String stripped = stripHtml(text);
        if (stripped.length() <= maxChars) return stripped;
        return stripped.substring(0, maxChars).trim() + "…";
    }

    /**
     * Clean WordPress excerpt (remove [&hellip;] artifacts)
     */
    public static String cleanExcerpt(String excerpt) {
        if (excerpt == null) return "";
        String clean = stripHtml(excerpt);
        clean = clean.replace("[&hellip;]", "…")
                     .replace("&#8230;", "…")
                     .replace("[...]", "…")
                     .trim();
        return clean;
    }

    /**
     * Decode HTML entities in title
     */
    public static String decodeTitle(String title) {
        if (title == null) return "";
        return fromHtml(title).toString().trim();
    }
}
