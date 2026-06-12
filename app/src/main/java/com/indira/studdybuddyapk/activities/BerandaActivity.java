package com.indira.studdybuddyapk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.adapters.TugasAdapter;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.TugasModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BerandaActivity extends AppCompatActivity {

    private RecyclerView rvDaftarTugas;
    private TugasAdapter tugasAdapter;
    private List<TugasModel> tugasList;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        databaseHelper = new DatabaseHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        tugasList = new ArrayList<>();

        SharedPreferences pref = getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        // Setup RecyclerView
        rvDaftarTugas = findViewById(R.id.rvDaftarTugas);
        if (rvDaftarTugas != null) {
            rvDaftarTugas.setLayoutManager(new LinearLayoutManager(this));
            tugasAdapter = new TugasAdapter(tugasList, this);
            rvDaftarTugas.setAdapter(tugasAdapter);
        }

        // Setup Bottom Navigation using the correct ID from activity_beranda.xml
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    loadTasks();
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    Intent intent = new Intent(BerandaActivity.this, AgendaActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    // Logic for settings or logout if needed
                    return true;
                }
                return false;
            });
            // Visual feedback for current tab
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        // Initial data load
        loadTasks();
    }

    private void loadTasks() {
        executorService.execute(() -> {
            List<TugasModel> tasks = databaseHelper.getAllTasks(userId);
            runOnUiThread(() -> {
                tugasList.clear();
                tugasList.addAll(tasks);
                if (tugasAdapter != null) {
                    tugasAdapter.notifyDataSetChanged();
                }
                updateStatistics(tasks);
            });
        });
    }

    private void updateStatistics(List<TugasModel> tasks) {
        int urgentCount = 0;
        int totalProgressSum = 0;

        for (TugasModel task : tasks) {
            if ("Tinggi".equals(task.getPrioritas())) {
                urgentCount++;
            }
            totalProgressSum += task.getProgress();
        }

        // Update stats in the UI using IDs from activity_beranda.xml
        // rtc8wiu7pei9 is the TextView for progress percentage in your XML
        TextView tvStat = findViewById(R.id.rtc8wiu7pei9);
        if (tvStat != null) {
            int averageProgress = tasks.isEmpty() ? 0 : totalProgressSum / tasks.size();
            tvStat.setText(averageProgress + "%");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
