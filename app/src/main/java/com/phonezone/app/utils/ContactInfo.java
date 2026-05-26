package com.phonezone.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ContactInfo {

    public static final String EMAIL = "admin@phonezone.biz.id";
    public static final String WEBSITE = "https://www.phonezone.biz.id";
    public static final String PHONE = "+62 800-0000-0000"; // ganti sesuai data asli
    public static final String INSTAGRAM = "https://instagram.com/phonezone";
    public static final String FACEBOOK = "https://facebook.com/phonezone";
    public static final String ADDRESS = "Indonesia";

    /**
     * Open email client
     */
    public static void sendEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback - PhoneZone App");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * Open browser to website
     */
    public static void openWebsite(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE));
        context.startActivity(intent);
    }

    /**
     * Open phone dialer
     */
    public static void callPhone(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE));
        context.startActivity(intent);
    }

    /**
     * Open Instagram
     */
    public static void openInstagram(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.instagram.android", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=phonezone"));
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM));
            context.startActivity(intent);
        }
    }

    /**
     * Share app
     */
    public static void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PhoneZone - Tech News App");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Baca berita teknologi terkini di PhoneZone!\n" + WEBSITE);
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan via"));
    }
}
