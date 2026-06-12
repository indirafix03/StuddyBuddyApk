package com.indira.studdybuddyapk.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.adapters.ChecklistAdapter;
import com.indira.studdybuddyapk.database.DatabaseHelper;
import com.indira.studdybuddyapk.models.ChecklistItemModel;
import java.util.List;

public class DetailTugasActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private LinearProgressIndicator progressIndicator;
    private TextView txtProgressPercent;
    private List<ChecklistItemModel> checklistItems;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tugas);

        db = new DatabaseHelper(this);
        taskId = getIntent().getIntExtra("task_id", -1);
        String taskTitle = getIntent().getStringExtra("task_title");
        String deadline = getIntent().getStringExtra("task_deadline");

        TextView txtTitle = findViewById(R.id.txtNamaMataKuliah);
        TextView txtDeadline = findViewById(R.id.txtBatasWaktu);
        progressIndicator = findViewById(R.id.progressIndicatorTugas);
        txtProgressPercent = findViewById(R.id.txtPersentaseProgress);
        
        txtTitle.setText(taskTitle);
        txtDeadline.setText("Deadline: " + deadline);

        RecyclerView rvChecklist = findViewById(R.id.rvChecklist);
        rvChecklist.setLayoutManager(new LinearLayoutManager(this));

        loadChecklist(rvChecklist);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        
        if (findViewById(R.id.btnSelesaiTugas) != null) {
            findViewById(R.id.btnSelesaiTugas).setOnClickListener(v -> finish());
        }
    }

    private void loadChecklist(RecyclerView rv) {
        checklistItems = db.getChecklistItems(taskId);
        ChecklistAdapter adapter = new ChecklistAdapter(checklistItems, item -> {
            db.updateChecklistItemStatus(item.getId(), item.isDone());
            updateProgress();
        });
        rv.setAdapter(adapter);
        updateProgress();
    }

    private void updateProgress() {
        if (checklistItems == null || checklistItems.isEmpty()) {
            progressIndicator.setProgress(0);
            txtProgressPercent.setText("0%");
            return;
        }

        int completed = 0;
        for (ChecklistItemModel item : checklistItems) {
            if (item.isDone()) {
                completed++;
            }
        }

        int percent = (completed * 100) / checklistItems.size();
        progressIndicator.setProgress(percent);
        txtProgressPercent.setText(percent + "%");
        
        // Opsional: Update status tugas utama jika semua checklist selesai
        db.updateTaskStatus(taskId, percent == 100);

        int finalPercent = percent;
        new Thread(() -> {
            db.updateTaskProgress(taskId, finalPercent);
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
