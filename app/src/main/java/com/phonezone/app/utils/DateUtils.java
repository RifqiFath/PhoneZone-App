package com.phonezone.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String WP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DISPLAY_FORMAT = "dd MMM yyyy";
    private static final String DISPLAY_FORMAT_FULL = "dd MMMM yyyy, HH:mm";

    /**
     * Convert WordPress date string to readable Indonesian format
     * e.g. "2024-01-15T10:30:00" → "15 Jan 2024"
     */
    public static String formatDate(String wpDate) {
        if (wpDate == null || wpDate.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(WP_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(DISPLAY_FORMAT, new Locale("id", "ID"));
            Date date = inputFormat.parse(wpDate);
            return date != null ? outputFormat.format(date) : wpDate;
        } catch (ParseException e) {
            return wpDate;
        }
    }

    /**
     * Full format with time
     */
    public static String formatDateFull(String wpDate) {
        if (wpDate == null || wpDate.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(WP_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(DISPLAY_FORMAT_FULL, new Locale("id", "ID"));
            Date date = inputFormat.parse(wpDate);
            return date != null ? outputFormat.format(date) : wpDate;
        } catch (ParseException e) {
            return wpDate;
        }
    }

    /**
     * Relative time: "2 hari lalu", "3 jam lalu", etc.
     */
    public static String getRelativeTime(String wpDate) {
        if (wpDate == null || wpDate.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(WP_DATE_FORMAT, Locale.getDefault());
            Date date = inputFormat.parse(wpDate);
            if (date == null) return wpDate;

            long now = System.currentTimeMillis();
            long diff = now - date.getTime();

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long weeks = days / 7;
            long months = days / 30;

            if (seconds < 60) return "Baru saja";
            if (minutes < 60) return minutes + " menit lalu";
            if (hours < 24) return hours + " jam lalu";
            if (days < 7) return days + " hari lalu";
            if (weeks < 4) return weeks + " minggu lalu";
            if (months < 12) return months + " bulan lalu";
            return formatDate(wpDate);

        } catch (ParseException e) {
            return formatDate(wpDate);
        }
    }
}
