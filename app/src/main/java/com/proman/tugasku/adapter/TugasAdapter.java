package com.proman.tugasku.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proman.tugasku.R;
import com.proman.tugasku.activity.UpdateActivity;
import com.proman.tugasku.model.Tugas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.TugasViewHolder> {

    private List<Tugas> listTugas;
    private Activity activity;

    public TugasAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setListTugas(List<Tugas> listTugas) {
        this.listTugas = listTugas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TugasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tugas, parent, false);
        return new TugasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TugasViewHolder holder, int position) {
        Tugas tugas = listTugas.get(position);

        holder.tvTitle.setText(tugas.getJudul());

        // Hitung selisih hari dan set teks deadline
        String deadlineText = getDeadlineText(tugas.getTanggalAkhir());
        holder.tvDeadline.setText(deadlineText);

        // Set warna text berdasarkan status deadline
        int textColor = getDeadlineColor(tugas.getTanggalAkhir());
        holder.tvDeadline.setTextColor(activity.getResources().getColor(textColor));

        // Set status tugas
        holder.tvStatus.setText(tugas.isSelesai() ? "SUDAH SELESAI" : "BELUM SELESAI");
        holder.tvStatus.setBackgroundResource(tugas.isSelesai() ?
                R.drawable.marktugas_sdh : R.drawable.marktugas_blm);

        // Handle klik item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, UpdateActivity.class);
            intent.putExtra("tugas", tugas);
            activity.startActivity(intent);
        });
    }

    private String getDeadlineText(Date deadlineDate) {
        Date currentDate = new Date();
        long diffInMillis = deadlineDate.getTime() - currentDate.getTime();
        long diffDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        if (diffDays > 0) {
            return diffDays + " Hari Lagi";
        } else if (diffDays == 0) {
            return "Deadline Hari Ini";
        } else {
            return "Terlewat " + Math.abs(diffDays) + " Hari";
        }
    }

    private int getDeadlineColor(Date deadlineDate) {
        Date currentDate = new Date();
        long diffInMillis = deadlineDate.getTime() - currentDate.getTime();
        long diffDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        if (diffDays > 3) {
            return R.color.warnhijau; // Masih lama
        } else if (diffDays >= 0) {
            return R.color.warnkuning; // Mendekati deadline
        } else {
            return R.color.warnmerah; // Sudah lewat
        }
    }

    @Override
    public int getItemCount() {
        return listTugas != null ? listTugas.size() : 0;
    }

    static class TugasViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDeadline, tvStatus;

        public TugasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
            tvStatus = itemView.findViewById(R.id.tv_marktugas);
        }
    }
}