package com.indira.studdybuddyapk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        holder.checkBox.setText(item.getItemText());
        holder.checkBox.setChecked(item.isDone());

        holder.checkBox.setOnClickListener(v -> {
            item.setDone(holder.checkBox.isChecked());
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return checklist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxItem);
        }
    }
}
