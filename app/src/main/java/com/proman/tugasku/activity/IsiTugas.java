package com.proman.tugasku.activity;

import android.app.DatePickerDialog;
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

public class IsiTugas extends AppCompatActivity {

    private dbhelper dbHelper;
    private EditText etJudul, etRincian, etTglAkhir;
    private RadioGroup rgStatus;
    private RadioButton rbBelumSelesai;
    private Button btnTambah;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_tugas);

        initializeViews();
        setupDatePickers();
        setupButtonListener();
    }

    private void initializeViews() {
        dbHelper = new dbhelper(this);

        etJudul = findViewById(R.id.edt_Judul);
        etRincian = findViewById(R.id.edt_rincian);
        etTglAkhir = findViewById(R.id.edt_deadline);

        rgStatus = findViewById(R.id.radio_status);
        rbBelumSelesai = findViewById(R.id.radio_belum);

        btnTambah = findViewById(R.id.btn_tambah);

        // Set default status
        rbBelumSelesai.setChecked(true);
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

    private void setupButtonListener() {
        btnTambah.setOnClickListener(v -> {
            if (validateInput()) {
                try {
                    // Parse input values
                    String judul = etJudul.getText().toString().trim();
                    String rincian = etRincian.getText().toString().trim();
                    Date tglAkhir = dateFormat.parse(etTglAkhir.getText().toString());
                    boolean isSelesai = rgStatus.getCheckedRadioButtonId() == R.id.radio_selesai;

                    // Create new task
                    Tugas tugas = new Tugas(judul, rincian, tglAkhir);
                    tugas.setSelesai(isSelesai);

                    // Add to database
                    long result = dbHelper.addTugas(tugas);

                    if (result != -1) {
                        showSuccessAndFinish();
                    } else {
                        Toast.makeText(this, "Gagal menambahkan tugas", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    Toast.makeText(this, "Format tanggal salah (gunakan dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (etJudul.getText().toString().trim().isEmpty()) {
            etJudul.setError("Judul tidak boleh kosong");
            isValid = false;
        }

        if (etRincian.getText().toString().trim().isEmpty()) {
            etRincian.setError("Rincian tidak boleh kosong");
            isValid = false;
        }

        if (etTglAkhir.getText().toString().isEmpty()) {
            etTglAkhir.setError("Deadline tidak boleh kosong");
            isValid = false;
        } else {
            try {
                dateFormat.parse(etTglAkhir.getText().toString());
            } catch (ParseException e) {
                etTglAkhir.setError("Format tanggal salah");
                isValid = false;
            }
        }

        return isValid;
    }

    private void showSuccessAndFinish() {
        Toast.makeText(this, "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}