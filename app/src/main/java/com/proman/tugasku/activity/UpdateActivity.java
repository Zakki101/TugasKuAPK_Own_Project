package com.proman.tugasku.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.proman.tugasku.R;
import com.proman.tugasku.db.dbhelper;
import com.proman.tugasku.model.Tugas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {
    private dbhelper dbhelper;
    private EditText etJudul, etRincian, etTglAkhir;
    private RadioGroup rgStatus;
    private RadioButton rbBelumSelesai, rbSudahSelesai;
    private Button btnUpdate, btnHapus;
    private Tugas tugas;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbhelper = new dbhelper(this);
        initializeViews();
        loadTaskData();
        setupDatePickers();
        setupButtonListeners();
    }

    private void initializeViews() {
        etJudul = findViewById(R.id.edt_Judul);
        etRincian = findViewById(R.id.edt_rincian);
        etTglAkhir = findViewById(R.id.edt_tanggal_akhir);
        rgStatus = findViewById(R.id.radio_status);
        rbBelumSelesai = findViewById(R.id.radio_belum);
        rbSudahSelesai = findViewById(R.id.radio_selesai);
        btnUpdate = findViewById(R.id.btn_update);
        btnHapus = findViewById(R.id.btn_delete);
    }

    private void loadTaskData() {
        tugas = (Tugas) getIntent().getSerializableExtra("tugas");
        if (tugas != null) {
            etJudul.setText(tugas.getJudul());
            etRincian.setText(tugas.getRincian());
            etTglAkhir.setText(dateFormat.format(tugas.getTanggalAkhir()));

            if (tugas.isSelesai()) {
                rbSudahSelesai.setChecked(true);
            } else {
                rbBelumSelesai.setChecked(true);
            }
        }
    }

    private void setupButtonListeners() {
        btnUpdate.setOnClickListener(v -> updateTask());

        btnHapus.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Hapus Tugas")
                .setMessage("Yakin ingin menghapus tugas ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteTask())
                .setNegativeButton("Tidak", null)
                .show());
    }
    private void setupDatePickers() {
        // Date picker for end date
        etTglAkhir.setOnClickListener(v -> showDatePicker(etTglAkhir));
    }
    private void showDatePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format: dd/MM/yyyy
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear);
                    editText.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateTask() {
        // Validate inputs
        if (etJudul.getText().toString().trim().isEmpty()) {
            etJudul.setError("Judul tidak boleh kosong");
            return;
        }

        try {
            // Parse dates
            Date tanggalAkhir = dateFormat.parse(etTglAkhir.getText().toString());

            // Update task object
            tugas.setJudul(etJudul.getText().toString());
            tugas.setRincian(etRincian.getText().toString());
            tugas.setTanggalAkhir(tanggalAkhir);
            tugas.setSelesai(rgStatus.getCheckedRadioButtonId() == R.id.radio_selesai);

            // Update in database
            dbhelper.updateTugas(tugas);
            Toast.makeText(this, "Tugas berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();

        } catch (ParseException e) {
            if (etTglAkhir.getText().toString().isEmpty()) {
                etTglAkhir.setError("Tanggal akhir harus diisi");
            } else {
                etTglAkhir.setError("Format tanggal salah (dd/MM/yyyy)");
            }
        }
    }

    private void deleteTask() {
        dbhelper.deleteTugas(tugas.getId());
        Toast.makeText(this, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}