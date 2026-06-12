package com.indira.studdybuddyapk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.indira.studdybuddyapk.R;
import com.indira.studdybuddyapk.models.MatakuliahModel;
import java.util.List;

public class MatakuliahAdapter extends RecyclerView.Adapter<MatakuliahAdapter.ViewHolder> {
    private List<MatakuliahModel> list;

    public MatakuliahAdapter(List<MatakuliahModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matakuliah_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatakuliahModel model = list.get(position);
        String info = model.getNamaMk() + "  |  " + model.getJamMulai() + " - " + model.getJamSelesai();
        holder.tvMatkulInfo.setText(info);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMatkulInfo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatkulInfo = itemView.findViewById(R.id.tvMatkulInfo);
        }
    }
}