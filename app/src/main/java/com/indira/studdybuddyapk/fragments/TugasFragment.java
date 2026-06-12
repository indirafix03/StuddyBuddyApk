package com.indira.studdybuddyapk.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.network.GeminiRequest;
import com.indira.studdybuddyapk.network.GeminiResponse;
import com.indira.studdybuddyapk.network.RetrofitClient;
import com.indira.studdybuddyapk.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TugasFragment extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    private TextView txtApiData;
    private View layoutError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tugas, container, false);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        txtApiData = view.findViewById(R.id.txtApiData);
        layoutError = view.findViewById(R.id.layoutError);

        swipeRefresh.setOnRefreshListener(this::fetchStudyTip);
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> fetchStudyTip());

        // Load data for the first time
        fetchStudyTip();

        return view;
    }

    private void fetchStudyTip() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
        if (layoutError != null) {
            layoutError.setVisibility(View.GONE);
        }

        // Cek koneksi internet sebelum melakukan request
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            showError();
            return;
        }

        String prompt = "Berikan satu tip belajar singkat dan motivasi untuk mahasiswa hari ini. Maksimal 3 kalimat.";
        GeminiRequest request = new GeminiRequest(prompt);

        RetrofitClient.getGeminiApiService().generateChecklist(RetrofitClient.GEMINI_API_KEY, request)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            String tip = response.body().getResponseText();
                            if (txtApiData != null) {
                                txtApiData.setText(tip);
                            }
                        } else {
                            showError();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                        showError();
                    }
                });
    }

    private void showError() {
        if (layoutError != null) {
            layoutError.setVisibility(View.VISIBLE);
        }
        if (getContext() != null) {
            Toast.makeText(getContext(), "Gagal memuat data. Periksa koneksi internet Anda.", Toast.LENGTH_SHORT).show();
        }
    }
}
