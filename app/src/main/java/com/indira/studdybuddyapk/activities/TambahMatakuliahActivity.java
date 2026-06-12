package com.indira.studdybuddyapk.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.MatakuliahModel;
import java.util.Calendar;
import java.util.Locale;

public class TambahMatakuliahActivity extends AppCompatActivity {
    private TextInputEditText etNamaMK, etJamMulai, etJamSelesai, etDosen, etRuangan;
    private ChipGroup chipGroupHari;
    private MaterialButton btnSave, btnCancel;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_matakuliah);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences pref = getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        initViews();
        setupTimePickers();

        // Handle prefilled day if coming from JadwalFragment dots menu
        if (getIntent().hasExtra("PREFILLED_DAY")) {
            String prefilledDay = getIntent().getStringExtra("PREFILLED_DAY");
            for (int i = 0; i < chipGroupHari.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupHari.getChildAt(i);
                if (chip.getText().toString().equalsIgnoreCase(prefilledDay)) {
                    chip.setChecked(true);
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> saveMatakuliah());
        btnCancel.setOnClickListener(v -> finish());
        if (findViewById(R.id.btnBack) != null) {
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        etNamaMK = findViewById(R.id.etNamaMK);
        etJamMulai = findViewById(R.id.inputJamMulai);
        etJamSelesai = findViewById(R.id.inputJamSelesai);
        etDosen = findViewById(R.id.inputNamaDosen);
        etRuangan = findViewById(R.id.inputRuangan);
        chipGroupHari = findViewById(R.id.chipGroupHari);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupTimePickers() {
        etJamMulai.setOnClickListener(v -> showTimePicker(etJamMulai));
        etJamSelesai.setOnClickListener(v -> showTimePicker(etJamSelesai));
    }

    private void showTimePicker(TextInputEditText editText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    editText.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveMatakuliah() {
        String nama = etNamaMK.getText().toString().trim();
        String jamMulai = etJamMulai.getText().toString().trim();
        String jamSelesai = etJamSelesai.getText().toString().trim();
        String dosen = etDosen.getText().toString().trim();
        String ruangan = etRuangan.getText().toString().trim();

        int checkedId = chipGroupHari.getCheckedChipId();
        if (nama.isEmpty() || jamMulai.isEmpty() || jamSelesai.isEmpty() || checkedId == -1) {
            Toast.makeText(this, "Mohon lengkapi semua field yang wajib", Toast.LENGTH_SHORT).show();
            return;
        }

        Chip selectedChip = findViewById(checkedId);
        String hari = selectedChip.getText().toString();

        MatakuliahModel model = new MatakuliahModel();
        model.setNamaMk(nama);
        model.setJadwal(hari);
        model.setJamMulai(jamMulai);
        model.setJamSelesai(jamSelesai);
        model.setDosen(dosen);
        model.setRuangan(ruangan);
        model.setUserId(userId);

        saveToDatabase(model);
    }

    private void saveToDatabase(MatakuliahModel model) {
        dbHelper.addMatakuliah(model);
        Toast.makeText(this, "Mata kuliah berhasil disimpan", Toast.LENGTH_SHORT).show();
        finish();
    }
}
