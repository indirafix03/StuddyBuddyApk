package com.indira.studdybuddyapk.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.models.ChecklistItemModel;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<ChecklistItemModel> checklist;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChecklistItemModel item);
    }

    public ChecklistAdapter(List<ChecklistItemModel> checklist, OnItemClickListener listener) {
        this.checklist = checklist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChecklistItemModel item = checklist.get(position);
        
        // Reset listener ke null untuk menghindari bug recycling
        holder.checkBox.setOnCheckedChangeListener(null);
        
        holder.tvChecklistText.setText(item.getItemText());
        holder.checkBox.setChecked(item.isDone());
        
        // Update gaya teks (strikethrough & alpha) hanya pada TextView agar Checkbox tetap terang
        updateTextStyle(holder.tvChecklistText, item.isDone());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setDone(isChecked);
            updateTextStyle(holder.tvChecklistText, isChecked);
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        // Memungkinkan klik pada teks untuk mengubah status checkbox
        holder.tvChecklistText.setOnClickListener(v -> holder.checkBox.toggle());
    }

    private void updateTextStyle(TextView textView, boolean isDone) {
        if (isDone) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(0.6f);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return checklist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvChecklistText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            tvChecklistText = itemView.findViewById(R.id.tvChecklistText);
        }
    }
}
