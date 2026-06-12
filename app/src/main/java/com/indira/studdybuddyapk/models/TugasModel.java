package com.indira.studdybuddyapk.models;

public class TugasModel {
    private int id;
    private int userId;
    private String namaTugas;
    private String deskripsi;
    private String deadline;
    private String prioritas;
    private boolean isCompleted;
    private int matakuliahId;
    private int progress;

    public TugasModel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNamaTugas() { return namaTugas; }
    public void setNamaTugas(String namaTugas) { this.namaTugas = namaTugas; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getPrioritas() { return prioritas; }
    public void setPrioritas(String prioritas) { this.prioritas = prioritas; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getMatakuliahId() { return matakuliahId; }
    public void setMatakuliahId(int matakuliahId) { this.matakuliahId = matakuliahId; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}