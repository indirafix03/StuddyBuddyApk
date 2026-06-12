package com.indira.studdybuddyapk.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.UserModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                if (dbHelper.checkUser(email)) {
                    Toast.makeText(this, "Email sudah terdaftar", Toast.LENGTH_SHORT).show();
                } else {
                    UserModel user = new UserModel();
                    user.setEmail(email);
                    user.setPassword(password);
                    long id = dbHelper.registerUser(user);
                    if (id > 0) {
                        Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            return false;
        }
        return true;
    }
}
