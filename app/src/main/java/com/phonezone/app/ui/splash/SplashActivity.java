package com.phonezone.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.phonezone.app.R;
import com.phonezone.app.ui.MainActivity;
import com.phonezone.app.ui.login.LoginActivity;
import com.phonezone.app.utils.AppPreferences;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Terapkan tema sebelum layout di-load
        AppPreferences prefs = AppPreferences.getInstance(this);
        if (prefs.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_splash);

        setContentView(R.layout.activity_splash);

        // Animate logo
        ImageView ivLogo = findViewById(R.id.iv_splash_logo);
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvTagline = findViewById(R.id.tv_tagline);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        ivLogo.startAnimation(fadeIn);
        tvAppName.startAnimation(slideUp);
        tvTagline.startAnimation(slideUp);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateNext();
        }, SPLASH_DELAY);
    }

    private void navigateNext() {
        AppPreferences prefs = AppPreferences.getInstance(this);
        prefs.logout();
        android.util.Log.d("DEBUG_SPLASH", "isLoggedIn: " + prefs.isLoggedIn());
        Intent intent;
        if (prefs.isLoggedIn()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
