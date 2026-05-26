package com.phonezone.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.phonezone.app.R;
import com.phonezone.app.ui.MainActivity;
import com.phonezone.app.utils.AppPreferences;

public class LoginActivity extends AppCompatActivity {

    // Dummy credentials
    private static final String DUMMY_USERNAME = "admin";
    private static final String DUMMY_PASSWORD = "123";
    private static final String DUMMY_USERNAME_2 = "user";
    private static final String DUMMY_PASSWORD_2 = "123";

    private EditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private Button btnLogin;
    private TextView tvSkip;
    private CheckBox cbShowPassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
        startAnimations();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSkip = findViewById(R.id.tv_skip);
        cbShowPassword = findViewById(R.id.cb_show_password);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvSkip.setOnClickListener(v -> {
            // Skip login - guest mode
            AppPreferences.getInstance(this).setLoggedIn(false, "Guest");
            goToMain();
        });

        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPasswordVisible = isChecked;
            if (isChecked) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void startAnimations() {
        ImageView ivLogo = findViewById(R.id.iv_logo);
        View cardLogin = findViewById(R.id.card_login);

        // Sembunyikan dulu sebelum animasi
        ivLogo.setAlpha(0f);
        cardLogin.setTranslationY(100f);
        cardLogin.setAlpha(0f);

        // Logo fade in
        ivLogo.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(100)
                .start();

        // Card slide up
        cardLogin.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(200)
                .start();
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Reset errors
        tilUsername.setError(null);
        tilPassword.setError(null);

        // Validation
        if (username.isEmpty()) {
            tilUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Check dummy credentials
        boolean valid = (username.equals(DUMMY_USERNAME) && password.equals(DUMMY_PASSWORD))
                || (username.equals(DUMMY_USERNAME_2) && password.equals(DUMMY_PASSWORD_2));

        if (valid) {
            AppPreferences.getInstance(this).setLoggedIn(true, username);
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            goToMain();
        } else {
            tilPassword.setError(getString(R.string.login_error));
            etPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
