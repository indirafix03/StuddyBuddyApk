package com.indira.studdybuddyapk.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.activities.DetailTugasActivity;
import com.indira.studdybuddyapk.activities.TambahTugasActivity;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.TugasModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.ViewHolder> {

    private List<TugasModel> tugasList;
    private Context context;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;
    private OnTaskChangeListener onTaskChangeListener;

    public interface OnTaskChangeListener {
        void onDataChanged();
    }

    public void setOnTaskChangeListener(OnTaskChangeListener listener) {
        this.onTaskChangeListener = listener;
    }

    public TugasAdapter(List<TugasModel> tugasList, Context context) {
        this.tugasList = tugasList;
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TugasModel tugas = tugasList.get(position);

        holder.txtJudulTugas.setText(tugas.getNamaTugas());
        holder.txtDeadlineTugas.setText("Deadline: " + tugas.getDeadline());

        // --- LOGIKA HITUNG SISA HARI DINAMIS ---
        long sisaHari = hitungSisaHari(tugas.getDeadline());

        if (sisaHari < 4) {
            holder.txtStatusTugas.setText("Urgent");
            holder.cardStatus.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FF5252")));
        } else if (sisaHari >= 3 && sisaHari <= 6) {
            holder.txtStatusTugas.setText("High Priority");
            holder.cardStatus.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        } else if (sisaHari >= 7 && sisaHari <= 14) {
            holder.txtStatusTugas.setText("Medium Priority");
            holder.cardStatus.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#2196F3")));
        } else {
            holder.txtStatusTugas.setText("Low Priority");
            holder.cardStatus.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

        // --- UPDATE UI BERDASARKAN STATUS SELESAI ---
        updateTextStyle(holder, tugas.isCompleted());

        // Set listener ke null agar setChecked tidak memicu event lama saat recycling
        holder.cbTugas.setOnCheckedChangeListener(null);
        holder.cbTugas.setChecked(tugas.isCompleted());

        holder.cbTugas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateTextStyle(holder, isChecked);
            executorService.execute(() -> {
                databaseHelper.updateTaskStatusCompletely(tugas.getId(), isChecked);
                tugas.setCompleted(isChecked);
                tugas.setProgress(isChecked ? 100 : 0);
                if (onTaskChangeListener != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> onTaskChangeListener.onDataChanged());
                }
            });
        });

        holder.cardTugas.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailTugasActivity.class);
            intent.putExtra("task_id", tugas.getId());
            intent.putExtra("task_title", tugas.getNamaTugas());
            intent.putExtra("task_deadline", tugas.getDeadline());
            intent.putExtra("task_description", tugas.getDeskripsi());
            intent.putExtra("task_priority", holder.txtStatusTugas.getText().toString());
            context.startActivity(intent);
        });

        holder.btnMore.setOnClickListener(v -> showActionDialog(tugas, holder.getAdapterPosition()));

        holder.cardTugas.setOnLongClickListener(v -> {
            showActionDialog(tugas, holder.getAdapterPosition());
            return true;
        });
    }

    private void updateTextStyle(ViewHolder holder, boolean isCompleted) {
        if (isCompleted) {
            holder.txtJudulTugas.setPaintFlags(holder.txtJudulTugas.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtJudulTugas.setAlpha(0.6f);
        } else {
            holder.txtJudulTugas.setPaintFlags(holder.txtJudulTugas.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtJudulTugas.setAlpha(1.0f);
        }
    }

    private void showActionDialog(TugasModel tugas, int position) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Action");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(context, TambahTugasActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("task_id", tugas.getId());
                intent.putExtra("task_title", tugas.getNamaTugas());
                intent.putExtra("task_deadline", tugas.getDeadline());
                intent.putExtra("task_description", tugas.getDeskripsi());
                context.startActivity(intent);
            } else {
                showDeleteConfirmation(tugas, position);
            }
        });
        builder.show();
    }

    private void showDeleteConfirmation(TugasModel tugas, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Tugas")
                .setMessage("Apakah Anda yakin ingin menghapus tugas ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    executorService.execute(() -> {
                        databaseHelper.deleteTask(tugas.getId());
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            if (position != RecyclerView.NO_POSITION && position < tugasList.size()) {
                                tugasList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, tugasList.size());
                                if (onTaskChangeListener != null) {
                                    onTaskChangeListener.onDataChanged();
                                }
                                Toast.makeText(context, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }

    private long hitungSisaHari(String deadlineString) {
        String pattern = "yyyy-MM-dd";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate deadlineDate = LocalDate.parse(deadlineString, formatter);
                LocalDate hariIni = LocalDate.now();
                return ChronoUnit.DAYS.between(hariIni, deadlineDate);
            } catch (Exception e) {
                return Long.MAX_VALUE;
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            try {
                Date deadlineDate = sdf.parse(deadlineString);
                Date hariIni = sdf.parse(sdf.format(new Date()));
                long diffInMillies = deadlineDate.getTime() - hariIni.getTime();
                return diffInMillies / (1000 * 60 * 60 * 24);
            } catch (ParseException e) {
                return Long.MAX_VALUE;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardTugas, cardStatus;
        CheckBox cbTugas;
        TextView txtJudulTugas, txtDeadlineTugas, txtStatusTugas;
        ImageView btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTugas = itemView.findViewById(R.id.cardTugas);
            cardStatus = itemView.findViewById(R.id.cardStatus);
            cbTugas = itemView.findViewById(R.id.cbTugas);
            txtJudulTugas = itemView.findViewById(R.id.txtJudulTugas);
            txtDeadlineTugas = itemView.findViewById(R.id.txtDeadlineTugas);
            txtStatusTugas = itemView.findViewById(R.id.txtStatusTugas);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
