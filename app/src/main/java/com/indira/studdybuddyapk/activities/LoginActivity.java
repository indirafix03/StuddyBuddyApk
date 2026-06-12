package com.indira.studdybuddyapk.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("StuddyBuddyPrefs", MODE_PRIVATE);

        // Cek jika sudah login sebelumnya, langsung ke MainActivity
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMain();
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = dbHelper.authenticateUser(email, password);
            if (userId != -1) {
                // Simpan status login dan userId
                sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("userEmail", email)
                        .putInt("userId", userId)
                        .apply();

                Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
