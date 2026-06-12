package com.indira.studdybuddyapk.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.MatakuliahModel;
import com.indira.studdybuddyapk.models.TugasModel;
import com.indira.studdybuddyapk.network.GeminiRequest;
import com.indira.studdybuddyapk.network.GeminiResponse;
import com.indira.studdybuddyapk.network.RetrofitClient;
import com.indira.studdybuddyapk.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahTugasActivity extends AppCompatActivity {

    private AutoCompleteTextView inputMataKuliah;
    private TextInputEditText inputJudulTugas, inputDeadline, inputDeskripsi;
    private MaterialButton btnGenerateAi, btnSave, btnCancel;
    private TextView txtHeaderTitle;
    private SwipeRefreshLayout swipeRefreshTambahTugas;
    private DatabaseHelper dbHelper;
    private List<String> generatedChecklist = new ArrayList<>();
    
    private boolean isEdit = false;
    private int taskId = -1;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tugas);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences pref = getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        initViews();
        setupCourseSpinner();

        // Check if editing
        if (getIntent().hasExtra("isEdit")) {
            isEdit = getIntent().getBooleanExtra("isEdit", false);
            if (isEdit) {
                taskId = getIntent().getIntExtra("task_id", -1);
                inputJudulTugas.setText(getIntent().getStringExtra("task_title"));
                inputDeadline.setText(getIntent().getStringExtra("task_deadline"));
                inputDeskripsi.setText(getIntent().getStringExtra("task_description"));
                
                if (txtHeaderTitle != null) {
                    txtHeaderTitle.setText("Edit Tugas");
                }
                btnSave.setText("Update Tugas");
                btnGenerateAi.setVisibility(android.view.View.GONE); 
                swipeRefreshTambahTugas.setEnabled(false); // Matikan swipe jika sedang edit
            }
        }

        swipeRefreshTambahTugas.setOnRefreshListener(this::generateAiChecklist);
        inputDeadline.setOnClickListener(v -> showDatePicker());
        btnGenerateAi.setOnClickListener(v -> generateAiChecklist());
        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
        findViewById(R.id.imgHeaderIcon).setOnClickListener(v -> finish());
    }

    private void initViews() {
        inputMataKuliah = findViewById(R.id.inputMataKuliah);
        inputJudulTugas = findViewById(R.id.inputJudulTugas);
        inputDeadline = findViewById(R.id.inputDeadline);
        inputDeskripsi = findViewById(R.id.inputDeskripsi);
        btnGenerateAi = findViewById(R.id.btnGenerateAi);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        swipeRefreshTambahTugas = findViewById(R.id.swipeRefreshTambahTugas);
    }

    private void setupCourseSpinner() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            List<MatakuliahModel> courses = dbHelper.getAllMatakuliah(userId);
            List<String> courseNames = new ArrayList<>();
            for (MatakuliahModel m : courses) {
                courseNames.add(m.getNamaMk());
            }
            handler.post(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(TambahTugasActivity.this, android.R.layout.simple_dropdown_item_1line, courseNames);
                inputMataKuliah.setAdapter(adapter);
            });
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            day
                    );
                    inputDeadline.setText(date);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void generateAiChecklist() {
        String matkul = inputMataKuliah.getText().toString();
        String judul = inputJudulTugas.getText().toString();
        String deskripsi = inputDeskripsi.getText().toString();

        if (judul.isEmpty()) {
            Toast.makeText(this, "Tolong masukkan judul tugas terlebih dahulu", Toast.LENGTH_SHORT).show();
            swipeRefreshTambahTugas.setRefreshing(false);
            return;
        }

        // Cek koneksi internet
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Tidak ada koneksi internet. Gagal generate checklist.", Toast.LENGTH_SHORT).show();
            swipeRefreshTambahTugas.setRefreshing(false);
            return;
        }

        swipeRefreshTambahTugas.setRefreshing(true);
        btnGenerateAi.setEnabled(false);
        btnGenerateAi.setText("Generating...");

        String prompt = String.format("Bertindaklah sebagai akademisi dan project manager yang handal. " +
                "Analisis tugas kuliah berikut dan buatkan task checklist yang konkret, logis, " +
                "dan berurutan sesuai alur kerja yang efektif (maksimal 6 item):\n\n" +
                "Mata Kuliah: %s\n" +
                "Judul: %s\n" +
                "Deskripsi: %s\n\n" +
                "Gunakan kata kerja aksi yang jelas (misal: Rancang, Implementasikan, Validasi). " +
                "Berikan hanya list itemnya saja, satu per baris tanpa nomor, simbol, atau penjelasan tambahan.",
                matkul, judul, deskripsi);

        GeminiRequest request = new GeminiRequest(prompt);

        RetrofitClient.getGeminiApiService().generateChecklist(RetrofitClient.GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                btnGenerateAi.setEnabled(true);
                btnGenerateAi.setText("Generate task checklist");
                swipeRefreshTambahTugas.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    String responseText = response.body().getResponseText();
                    if (!responseText.isEmpty()) {
                        generatedChecklist.clear();
                        String[] lines = responseText.split("\n");
                        for (String line : lines) {
                            if (!line.trim().isEmpty()) {
                                String item = line.trim().replaceAll("^[-*•\\d.\\s]+", "");
                                if (!item.isEmpty()) {
                                    generatedChecklist.add(item);
                                }
                            }
                        }
                        Toast.makeText(TambahTugasActivity.this, "Checklist berhasil dibuat oleh AI!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TambahTugasActivity.this, "Gagal mendapatkan respon AI", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TambahTugasActivity.this, "Error AI: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                btnGenerateAi.setEnabled(true);
                btnGenerateAi.setText("Generate task checklist");
                swipeRefreshTambahTugas.setRefreshing(false);
                Toast.makeText(TambahTugasActivity.this, "Gagal menghubungi AI: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTask() {
        String judul = inputJudulTugas.getText().toString();
        String deskripsi = inputDeskripsi.getText().toString();
        String deadline = inputDeadline.getText().toString();

        if (judul.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Judul dan Deadline wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            TugasModel task = new TugasModel();
            task.setNamaTugas(judul);
            task.setDeskripsi(deskripsi);
            task.setDeadline(deadline);
            task.setPrioritas("Tinggi");
            task.setUserId(userId);

            if (isEdit) {
                task.setId(taskId);
                dbHelper.updateTask(task);
                handler.post(() -> {
                    Toast.makeText(TambahTugasActivity.this, "Tugas berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                long newId = dbHelper.addTask(task);

                if (newId != -1) {
                    for (String item : generatedChecklist) {
                        dbHelper.addChecklistItem((int) newId, item);
                    }
                    handler.post(() -> {
                        Toast.makeText(TambahTugasActivity.this, "Tugas berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    handler.post(() -> Toast.makeText(TambahTugasActivity.this, "Gagal menyimpan tugas", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
