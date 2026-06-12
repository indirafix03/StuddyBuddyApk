package com.indira.studdybuddyapk.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.activities.TambahMatakuliahActivity;
import com.indira.studdybuddyapk.adapters.MatakuliahAdapter;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.MatakuliahModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class JadwalFragment extends Fragment {
    private RecyclerView rvJadwalToday;
    private TextView tvHariBesar, tvTanggalBesar, tvNoSchedule;
    private ImageButton btnAddAgenda;
    private DatabaseHelper dbHelper;
    private MatakuliahAdapter adapter;
    private List<MatakuliahModel> todaySchedule = new ArrayList<>();
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jadwal, container, false);

        dbHelper = new DatabaseHelper(getContext());
        SharedPreferences pref = getActivity().getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        initViews(view);
        setupRecyclerView();
        setupDayCards(view);
        updateDateTime();
        loadTodaySchedule();

        btnAddAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TambahMatakuliahActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void initViews(View v) {
        rvJadwalToday = v.findViewById(R.id.rvJadwalToday);
        tvHariBesar = v.findViewById(R.id.tvHariBesar);
        tvTanggalBesar = v.findViewById(R.id.tvTanggalBesar);
        tvNoSchedule = v.findViewById(R.id.tvNoSchedule);
        btnAddAgenda = v.findViewById(R.id.btn_star_agenda);
    }

    private void setupRecyclerView() {
        rvJadwalToday.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MatakuliahAdapter(todaySchedule);
        rvJadwalToday.setAdapter(adapter);
    }

    private void setupDayCards(View v) {
        setupCardAction(v.findViewById(R.id.cardMonday), v.findViewById(R.id.btnDotsMonday), "Monday");
        setupCardAction(v.findViewById(R.id.cardTuesday), v.findViewById(R.id.btnDotsTuesday), "Tuesday");
        setupCardAction(v.findViewById(R.id.cardWednesday), v.findViewById(R.id.btnDotsWednesday), "Wednesday");
        setupCardAction(v.findViewById(R.id.cardThursday), v.findViewById(R.id.btnDotsThursday), "Thursday");
        setupCardAction(v.findViewById(R.id.cardFriday), v.findViewById(R.id.btnDotsFriday), "Friday");
        setupCardAction(v.findViewById(R.id.cardSaturday), v.findViewById(R.id.btnDotsSaturday), "Saturday");
        setupCardAction(v.findViewById(R.id.cardSunday), v.findViewById(R.id.btnDotsSunday), "Sunday");
    }

    private void setupCardAction(MaterialCardView card, ImageView dots, String day) {
        if (card == null) return;

        card.setOnLongClickListener(v -> {
            showDaySchedulePopup(day);
            return true;
        });

        if (dots != null) {
            dots.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(getContext(), dots);
                popup.getMenu().add("Add Course (" + day + ")");
                popup.getMenu().add("Clear Schedule (" + day + ")");
                
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().toString().contains("Add Course")) {
                        Intent intent = new Intent(getActivity(), TambahMatakuliahActivity.class);
                        intent.putExtra("PREFILLED_DAY", day);
                        startActivity(intent);
                    } else if (item.getTitle().toString().contains("Clear Schedule")) {
                        confirmClearSchedule(day);
                    }
                    return true;
                });
                popup.show();
            });
        }
    }

    private void showDaySchedulePopup(String day) {
        List<MatakuliahModel> all = dbHelper.getAllMatakuliah(userId);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        
        for (MatakuliahModel m : all) {
            if (m.getJadwal() != null && m.getJadwal().equalsIgnoreCase(day)) {
                count++;
                sb.append(count).append(". ").append(m.getNamaMk())
                  .append("\n   ").append(m.getJamMulai()).append(" - ").append(m.getJamSelesai())
                  .append(m.getRuangan() != null && !m.getRuangan().isEmpty() ? " | " + m.getRuangan() : "")
                  .append("\n\n");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Schedule for " + day);
        if (count == 0) {
            builder.setMessage("No schedule found for this day.");
        } else {
            builder.setMessage(sb.toString().trim());
        }
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void confirmClearSchedule(String day) {
        new AlertDialog.Builder(getContext())
                .setTitle("Clear Schedule")
                .setMessage("Delete all courses for " + day + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteMatakuliahByDay(day, userId);
                    Toast.makeText(getContext(), "Schedule for " + day + " cleared", Toast.LENGTH_SHORT).show();
                    loadTodaySchedule();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);

        tvHariBesar.setText(dayFormat.format(calendar.getTime()));
        tvTanggalBesar.setText(dateFormat.format(calendar.getTime()));
    }

    private void loadTodaySchedule() {
        Calendar calendar = Calendar.getInstance();
        String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());

        List<MatakuliahModel> allMatkul = dbHelper.getAllMatakuliah(userId);
        todaySchedule.clear();

        for (MatakuliahModel m : allMatkul) {
            if (m.getJadwal() != null && m.getJadwal().equalsIgnoreCase(currentDay)) {
                todaySchedule.add(m);
            }
        }

        if (todaySchedule.isEmpty()) {
            tvNoSchedule.setVisibility(View.VISIBLE);
            rvJadwalToday.setVisibility(View.GONE);
        } else {
            tvNoSchedule.setVisibility(View.GONE);
            rvJadwalToday.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodaySchedule();
    }
}
