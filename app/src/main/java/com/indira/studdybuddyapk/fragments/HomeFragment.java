package com.indira.studdybuddyapk.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.activities.DetailTugasActivity;
import com.indira.studdybuddyapk.activities.TambahTugasActivity;
import com.indira.studdybuddyapk.adapters.TugasAdapter;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.TugasModel;
import com.indira.studdybuddyapk.network.GeminiRequest;
import com.indira.studdybuddyapk.network.GeminiResponse;
import com.indira.studdybuddyapk.network.RetrofitClient;
import com.indira.studdybuddyapk.utils.NetworkUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView rvTasks;
    private DatabaseHelper dbHelper;
    private TugasAdapter adapter;
    private List<TugasModel> tugasList = new ArrayList<>();
    private TugasModel nearestTask = null;

    private TextView tvWelcome, tvNearestTask, tvDaysLeft, tvProgressPercent, tvCurrentDate;
    private TextView tvUrgent, tvThisWeek, tvIncomplete, tvAiQuote, btnRetryHome, tvTotalTasksCount;
    private ImageView btnDetailNearest;
    private FloatingActionButton fabAdd;
    private LinearProgressIndicator progressIndicatorOverall;
    private SwipeRefreshLayout swipeRefreshHome;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new DatabaseHelper(getContext());

        SharedPreferences pref = getActivity().getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        initViews(view);
        setupRecyclerView();
        setCurrentDate();

        swipeRefreshHome.setOnRefreshListener(() -> refreshAllData(true));
        btnRetryHome.setOnClickListener(v -> fetchAiQuote(true));

        if (btnDetailNearest != null) {
            btnDetailNearest.setOnClickListener(v -> {
                if (nearestTask != null) {
                    navigateToDetail(nearestTask);
                }
            });
        }

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TambahTugasActivity.class);
                startActivity(intent);
            });
        }

        refreshAllData(false);
        return view;
    }

    private void initViews(View v) {
        rvTasks = v.findViewById(R.id.rvTasks);
        tvWelcome = v.findViewById(R.id.rua91bi9lv9n);
        tvNearestTask = v.findViewById(R.id.r9fsqdo9qjgc);
        tvDaysLeft = v.findViewById(R.id.ruhkgmwn502r);
        tvProgressPercent = v.findViewById(R.id.rtc8wiu7pei9);
        tvUrgent = v.findViewById(R.id.tvUrgentCount);
        tvThisWeek = v.findViewById(R.id.tvWeekDeadlineCount);
        tvIncomplete = v.findViewById(R.id.tvIncompleteCount);
        tvTotalTasksCount = v.findViewById(R.id.tvTotalTasksCount);
        tvCurrentDate = v.findViewById(R.id.tvCurrentDate);
        tvAiQuote = v.findViewById(R.id.tvAiQuote);
        btnRetryHome = v.findViewById(R.id.btnRetryHome);
        btnDetailNearest = v.findViewById(R.id.card);
        fabAdd = v.findViewById(R.id.fabAdd);
        progressIndicatorOverall = v.findViewById(R.id.progressIndicatorOverall);
        swipeRefreshHome = v.findViewById(R.id.swipeRefreshHome);
    }

    private void setCurrentDate() {
        if (tvCurrentDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            tvCurrentDate.setText(currentDate);
        }
    }

    private void refreshAllData(boolean forceRefreshAi) {
        swipeRefreshHome.setRefreshing(true);
        loadData();
        fetchAiQuote(forceRefreshAi);
    }

    private void fetchAiQuote(boolean forceRefresh) {
        SharedPreferences pref = getActivity().getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
        String cachedQuote = pref.getString("last_ai_quote", "Menunggu kutipan hari ini...");

        tvAiQuote.setText(cachedQuote);

        if (!forceRefresh) {
            checkFinishRefreshing();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "Gagal memperbarui kutipan. Periksa koneksi internet Anda.", Toast.LENGTH_SHORT).show();
            btnRetryHome.setVisibility(View.VISIBLE);
            checkFinishRefreshing();
            return;
        }

        btnRetryHome.setVisibility(View.GONE);
        String prompt = "Berikan satu kutipan motivasi singkat dari filsuf atau tokoh berpengaruh dunia tentang pendidikan atau kegigihan. Sertakan nama tokohnya. Maksimal 2 kalimat.";
        GeminiRequest request = new GeminiRequest(prompt);

        RetrofitClient.getGeminiApiService().generateChecklist(RetrofitClient.GEMINI_API_KEY, request)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String quote = response.body().getResponseText();
                            tvAiQuote.setText(quote);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("last_ai_quote", quote);
                            editor.apply();
                        } else {
                            btnRetryHome.setVisibility(View.VISIBLE);
                        }
                        checkFinishRefreshing();
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        btnRetryHome.setVisibility(View.VISIBLE);
                        checkFinishRefreshing();
                    }
                });
    }

    private void checkFinishRefreshing() {
        if (swipeRefreshHome != null && swipeRefreshHome.isRefreshing()) {
            swipeRefreshHome.setRefreshing(false);
        }
    }

    private void navigateToDetail(TugasModel task) {
        Intent intent = new Intent(getContext(), DetailTugasActivity.class);
        intent.putExtra("task_id", task.getId());
        intent.putExtra("task_title", task.getNamaTugas());
        intent.putExtra("task_deadline", task.getDeadline());
        intent.putExtra("task_description", task.getDeskripsi());
        intent.putExtra("task_priority", task.getPrioritas());
        startActivity(intent);
    }

    private void setupRecyclerView() {
        if (rvTasks != null) {
            rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new TugasAdapter(tugasList, getContext());
            adapter.setOnTaskChangeListener(() -> {
                // Refresh data when task changes to update overall progress and counts
                loadData();
            });
            rvTasks.setAdapter(adapter);
        }
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            SharedPreferences pref = getActivity().getSharedPreferences("StuddyBuddyPrefs", Context.MODE_PRIVATE);
            String email = pref.getString("userEmail", "User");
            String name = email.contains("@") ? email.split("@")[0] : email;

            List<TugasModel> tasks = dbHelper.getAllTasks(userId);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (tvWelcome != null) {
                    tvWelcome.setText("Welcome back " + name + ", Ready to smash your goals today?");
                }
                tugasList.clear();
                tugasList.addAll(tasks);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                calculateStats(tasks);
                checkFinishRefreshing();
            });
        });
    }

    private void calculateStats(List<TugasModel> tasks) {
        int urgent = 0, incomplete = 0, thisWeek = 0, totalProgressSum = 0;
        nearestTask = null;
        long minDiff = Long.MAX_VALUE;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar now = Calendar.getInstance();

        for (TugasModel t : tasks) {
            if (!t.isCompleted()) incomplete++;
            totalProgressSum += t.getProgress();

            try {
                Date d = sdf.parse(t.getDeadline());
                if (d != null) {
                    long diffMillis = d.getTime() - now.getTimeInMillis();
                    long diff = diffMillis / (24 * 60 * 60 * 1000);
                    if (diff >= 0 && diff < 3) urgent++;
                    if (diff >= 0 && diff <= 6) thisWeek++;
                    
                    if (t.getProgress() < 100 && diff >= 0 && diff < minDiff) {
                        minDiff = diff;
                        nearestTask = t;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (tvUrgent != null) tvUrgent.setText(String.valueOf(urgent));
        if (tvIncomplete != null) tvIncomplete.setText(String.valueOf(incomplete));
        if (tvThisWeek != null) tvThisWeek.setText(String.valueOf(thisWeek));
        if (tvTotalTasksCount != null) tvTotalTasksCount.setText("(" + tasks.size() + ")");

        if (nearestTask != null) {
            if (tvNearestTask != null) tvNearestTask.setText(nearestTask.getNamaTugas().toUpperCase());
            long days = minDiff;
            if (tvDaysLeft != null) tvDaysLeft.setText(days + (days <= 1 ? " Day left" : " Days left"));
            if (btnDetailNearest != null) btnDetailNearest.setVisibility(View.VISIBLE);
        } else {
            if (tvNearestTask != null) tvNearestTask.setText("TIDAK ADA TUGAS");
            if (tvDaysLeft != null) tvDaysLeft.setText("-");
            if (btnDetailNearest != null) btnDetailNearest.setVisibility(View.GONE);
        }

        if (!tasks.isEmpty()) {
            int overallProgress = totalProgressSum / tasks.size();
            if (tvProgressPercent != null) tvProgressPercent.setText(overallProgress + "%");
            if (progressIndicatorOverall != null) progressIndicatorOverall.setProgress(overallProgress);
        } else {
            if (tvProgressPercent != null) tvProgressPercent.setText("0%");
            if (progressIndicatorOverall != null) progressIndicatorOverall.setProgress(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
