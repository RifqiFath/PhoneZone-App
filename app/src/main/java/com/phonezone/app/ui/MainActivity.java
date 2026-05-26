package com.phonezone.app.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.phonezone.app.R;
import com.phonezone.app.ui.bookmark.BookmarkFragment;
import com.phonezone.app.ui.category.CategoryFragment;
import com.phonezone.app.ui.home.HomeFragment;
import com.phonezone.app.ui.search.SearchFragment;
import com.phonezone.app.ui.settings.SettingsFragment;
import com.phonezone.app.utils.AppPreferences;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Fragment currentFragment;

    // Fragment tags
    private static final String TAG_HOME = "home";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_BOOKMARK = "bookmark";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_SETTINGS = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply dark mode setting
        AppPreferences prefs = AppPreferences.getInstance(this);
        if (prefs.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        // Load Home fragment on start
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), TAG_HOME);
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment(), TAG_HOME);
                return true;
            } else if (id == R.id.nav_category) {
                loadFragment(new CategoryFragment(), TAG_CATEGORY);
                return true;
            } else if (id == R.id.nav_bookmark) {
                loadFragment(new BookmarkFragment(), TAG_BOOKMARK);
                return true;
            } else if (id == R.id.nav_search) {
                loadFragment(new SearchFragment(), TAG_SEARCH);
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new SettingsFragment(), TAG_SETTINGS);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        Fragment existing = getSupportFragmentManager().findFragmentByTag(tag);
        if (existing != null) {
            transaction.replace(R.id.fragment_container, existing, tag);
        } else {
            transaction.replace(R.id.fragment_container, fragment, tag);
        }
        transaction.commit();
        currentFragment = fragment;
    }

    /**
     * Navigate to a specific tab from elsewhere (e.g. HomeFragment → CategoryFragment)
     */
    public void navigateTo(int menuItemId) {
        bottomNav.setSelectedItemId(menuItemId);
    }

    @Override
    public void onBackPressed() {
        // If not on home, go back to home
        if (!(currentFragment instanceof HomeFragment)) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}
