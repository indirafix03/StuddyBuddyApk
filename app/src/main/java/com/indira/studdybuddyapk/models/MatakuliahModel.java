package com.indira.studdybuddyapk.models;

public class MatakuliahModel {
    private int id;
    private int userId;
    private String kodeMk;
    private String namaMk;
    private String jadwal;
    private String dosen;
    private String ruangan;
    private String jamMulai;
    private String jamSelesai;

    public MatakuliahModel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getKodeMk() { return kodeMk; }
    public void setKodeMk(String kodeMk) { this.kodeMk = kodeMk; }

    public String getNamaMk() { return namaMk; }
    public void setNamaMk(String namaMk) { this.namaMk = namaMk; }

    public String getJadwal() { return jadwal; }
    public void setJadwal(String jadwal) { this.jadwal = jadwal; }

    public String getDosen() { return dosen; }
    public void setDosen(String dosen) { this.dosen = dosen; }

    public String getRuangan() { return ruangan; }
    public void setRuangan(String ruangan) { this.ruangan = ruangan; }

    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }

    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }
}