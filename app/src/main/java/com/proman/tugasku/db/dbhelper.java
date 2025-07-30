package com.proman.tugasku.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.proman.tugasku.model.Tugas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class dbhelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dbtugas";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TUGAS = "tugas";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_JUDUL = "judul";
    private static final String KEY_DESKRIPSI = "deskripsi";
    private static final String KEY_TGL_AKHIR = "tanggal_akhir";
    private static final String KEY_SELESAI = "selesai";

    // Date format for database storage
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // SQL to create table
    private static final String CREATE_TABLE_TUGAS = "CREATE TABLE "
            + TABLE_TUGAS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_JUDUL + " TEXT NOT NULL,"
            + KEY_DESKRIPSI + " TEXT,"
            + KEY_TGL_AKHIR + " TEXT NOT NULL,"
            + KEY_SELESAI + " INTEGER DEFAULT 0);"; // 0 = not completed, 1 = completed

    public dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TUGAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUGAS);
        onCreate(db);
    }

    // Add a new task
    public long addTugas(Tugas tugas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JUDUL, tugas.getJudul());
        values.put(KEY_DESKRIPSI, tugas.getRincian());
        values.put(KEY_TGL_AKHIR, dateFormat.format(tugas.getTanggalAkhir()));
        values.put(KEY_SELESAI, tugas.isSelesai() ? 1 : 0);

        long id = db.insert(TABLE_TUGAS, null, values);
        db.close();

        return id;
    }

    // Get all tasks
    @SuppressLint("Range")
    public ArrayList<Tugas> getAllTugas() {
        ArrayList<Tugas> tugasList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TUGAS + " ORDER BY " + KEY_TGL_AKHIR + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Tugas tugas = new Tugas();
                tugas.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                tugas.setJudul(cursor.getString(cursor.getColumnIndex(KEY_JUDUL)));
                tugas.setRincian(cursor.getString(cursor.getColumnIndex(KEY_DESKRIPSI)));

                try {
                    String tglAkhirStr = cursor.getString(cursor.getColumnIndex(KEY_TGL_AKHIR));
                    Date tglAkhir = dateFormat.parse(tglAkhirStr);
                    tugas.setTanggalAkhir(tglAkhir);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                tugas.setSelesai(cursor.getInt(cursor.getColumnIndex(KEY_SELESAI)) == 1);

                tugasList.add(tugas);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tugasList;
    }

    // Update a task
    public int updateTugas(Tugas tugas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JUDUL, tugas.getJudul());
        values.put(KEY_DESKRIPSI, tugas.getRincian());
        values.put(KEY_TGL_AKHIR, dateFormat.format(tugas.getTanggalAkhir()));
        values.put(KEY_SELESAI, tugas.isSelesai() ? 1 : 0);

        int rowsAffected = db.update(TABLE_TUGAS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(tugas.getId())});
        db.close();

        return rowsAffected;
    }

    // Delete a task
    public void deleteTugas(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TUGAS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Get a single task by ID
    @SuppressLint("Range")
    public Tugas getTugas(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Tugas tugas = null;

        Cursor cursor = db.query(TABLE_TUGAS,
                new String[]{KEY_ID, KEY_JUDUL, KEY_DESKRIPSI, KEY_TGL_AKHIR, KEY_SELESAI},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            tugas = new Tugas();
            tugas.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            tugas.setJudul(cursor.getString(cursor.getColumnIndex(KEY_JUDUL)));
            tugas.setRincian(cursor.getString(cursor.getColumnIndex(KEY_DESKRIPSI)));

            try {
                String tglAkhirStr = cursor.getString(cursor.getColumnIndex(KEY_TGL_AKHIR));
                Date tglAkhir = dateFormat.parse(tglAkhirStr);
                tugas.setTanggalAkhir(tglAkhir);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tugas.setSelesai(cursor.getInt(cursor.getColumnIndex(KEY_SELESAI)) == 1);
            cursor.close();
        }

        db.close();
        return tugas;
    }
}