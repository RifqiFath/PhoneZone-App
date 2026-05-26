package com.phonezone.app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.phonezone.app.R;
import com.phonezone.app.ui.login.LoginActivity;
import com.phonezone.app.utils.AppPreferences;
import com.phonezone.app.utils.ContactInfo;

public class SettingsFragment extends Fragment {

    private AppPreferences prefs;
    private TextView tvUsername, tvUserRole;
    private SwitchMaterial switchDarkMode, switchNotifications;
    private LinearLayout btnContact, btnAbout, btnLogout, btnShare, btnWebsite;
    private LinearLayout btnPrivacy, btnFaq;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = AppPreferences.getInstance(requireContext());
        initViews(view);
        loadUserData();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvUserRole = view.findViewById(R.id.tv_user_role);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        btnContact = view.findViewById(R.id.btn_contact);
        btnAbout = view.findViewById(R.id.btn_about);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnShare = view.findViewById(R.id.btn_share);
        btnWebsite = view.findViewById(R.id.btn_website);
        btnPrivacy = view.findViewById(R.id.btn_privacy);
        btnFaq = view.findViewById(R.id.btn_faq);

        // Set initial state
        switchDarkMode.setChecked(prefs.isDarkMode());
        switchNotifications.setChecked(prefs.isNotificationsEnabled());
    }

    private void loadUserData() {
        if (prefs.isLoggedIn()) {
            tvUsername.setText(prefs.getUsername());
            tvUserRole.setText("Member");
            if (btnLogout != null) btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvUsername.setText("Guest");
            tvUserRole.setText("Tamu");
            if (btnLogout != null) {
                // Show login button instead
                TextView tvLogout = btnLogout.findViewById(R.id.tv_logout_label);
                if (tvLogout != null) tvLogout.setText("Masuk / Daftar");
            }
        }
    }

    private void setupClickListeners() {
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setDarkMode(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setNotifications(isChecked);
            Toast.makeText(requireContext(),
                    isChecked ? "Notifikasi diaktifkan" : "Notifikasi dinonaktifkan",
                    Toast.LENGTH_SHORT).show();
        });

        btnContact.setOnClickListener(v -> showContactDialog());
        btnAbout.setOnClickListener(v -> showAboutDialog());
        btnShare.setOnClickListener(v -> ContactInfo.shareApp(requireContext()));
        btnWebsite.setOnClickListener(v -> ContactInfo.openWebsite(requireContext()));

        btnPrivacy.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Kebijakan Privasi", Toast.LENGTH_SHORT).show();
        });

        btnFaq.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "FAQ", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            if (prefs.isLoggedIn()) {
                showLogoutDialog();
            } else {
                goToLogin();
            }
        });
    }

    private void showContactDialog() {
        View contactView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_contact, null);

        contactView.findViewById(R.id.btn_email).setOnClickListener(v -> {
            ContactInfo.sendEmail(requireContext());
        });
        contactView.findViewById(R.id.btn_website_contact).setOnClickListener(v -> {
            ContactInfo.openWebsite(requireContext());
        });
        contactView.findViewById(R.id.btn_instagram).setOnClickListener(v -> {
            ContactInfo.openInstagram(requireContext());
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.contact_title))
                .setView(contactView)
                .setPositiveButton("Tutup", null)
                .show();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.about_title))
                .setMessage(getString(R.string.about_desc)
                        + "\n\n" + getString(R.string.version_info)
                        + "\n" + getString(R.string.developer))
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin keluar dari akun?")
                .setPositiveButton("Keluar", (dialog, which) -> {
                    prefs.logout();
                    goToLogin();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void goToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
