package com.proman.tugasku.model;

import java.io.Serializable;
import java.util.Date;

public class Tugas implements Serializable {
    private int id;
    private String judul, rincian;
    private Date tanggalAkhir;
    private Boolean selesai;

    public Tugas(String judul, String rincian, Date tanggalAkhir) {
        this.judul = judul;
        this.rincian = rincian;
        this.tanggalAkhir = tanggalAkhir;
        this.selesai = false;
    }

    public Tugas() {

    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getRincian() {
        return rincian;
    }

    public void setRincian(String rincian) {
        this.rincian = rincian;
    }

    public Date getTanggalAkhir() {
        return tanggalAkhir;
    }

    public void setTanggalAkhir(Date tanggalAkhir) {
        this.tanggalAkhir = tanggalAkhir;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public void setSelesai(boolean selesai) {
        this.selesai = selesai;
    }

    // Helper method untuk status
    public String getStatusText() {
        return selesai ? "Selesai" : "Belum Selesai";
    }

    // Helper method untuk cek deadline
    public boolean isTerlambat() {
        Date sekarang = new Date();
        return !selesai && sekarang.after(tanggalAkhir);
    }

    @Override
    public String toString() {
        return "Tugas{" +
                "id=" + id +
                ", judul='" + judul + '\'' +
                ", deadline='" + tanggalAkhir.toString() + '\'' +
                ", status=" + getStatusText() +
                '}';
    }
}


