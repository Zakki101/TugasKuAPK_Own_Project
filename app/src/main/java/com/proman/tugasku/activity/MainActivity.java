package com.proman.tugasku.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proman.tugasku.R;
import com.proman.tugasku.adapter.TugasAdapter;
import com.proman.tugasku.db.dbhelper;
import com.proman.tugasku.model.Tugas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton btntambahtask, btnsort;
    private RecyclerView recyclerView;
    private TugasAdapter adapter;
    private ArrayList<Tugas> tugasArrayList;
    private dbhelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        loadDataTugas();
        setupButtonListeners();
    }

    private void initializeViews() {
        btntambahtask = findViewById(R.id.btnaddtugas);
        btnsort = findViewById(R.id.btn_sort);
        recyclerView = findViewById(R.id.rview);
    }

    private void setupRecyclerView() {
        adapter = new TugasAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadDataTugas() {
        dbHelper = new dbhelper(this);
        tugasArrayList = dbHelper.getAllTugas();
        adapter.setListTugas(tugasArrayList);
    }

    private void setupButtonListeners() {
        btntambahtask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, IsiTugas.class);
            startActivity(intent);
        });

        btnsort.setOnClickListener(this::showSortMenu);
    }

    private void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.sort_by_date_asc) {
                sortByDate(true);
                return true;
            } else if (itemId == R.id.sort_by_date_desc) {
                sortByDate(false);
                return true;
            } else if (itemId == R.id.sort_by_name) {
                sortByName();
                return true;
            } else if (itemId == R.id.sort_by_status) {
                sortByStatus();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void sortByDate(boolean ascending) {
        List<Tugas> sortedList = new ArrayList<>(tugasArrayList);
        Collections.sort(sortedList, (t1, t2) -> {
            if (ascending) {
                return t1.getTanggalAkhir().compareTo(t2.getTanggalAkhir());
            } else {
                return t2.getTanggalAkhir().compareTo(t1.getTanggalAkhir());
            }
        });
        adapter.setListTugas(sortedList);
    }

    private void sortByName() {
        List<Tugas> sortedList = new ArrayList<>(tugasArrayList);
        Collections.sort(sortedList, (t1, t2) ->
                t1.getJudul().compareToIgnoreCase(t2.getJudul()));
        adapter.setListTugas(sortedList);
    }

    private void sortByStatus() {
        List<Tugas> sortedList = new ArrayList<>(tugasArrayList);
        Collections.sort(sortedList, (t1, t2) -> {
            // Belum selesai muncul pertama
            if (t1.isSelesai() == t2.isSelesai()) return 0;
            return t1.isSelesai() ? 1 : -1;
        });
        adapter.setListTugas(sortedList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        tugasArrayList = dbHelper.getAllTugas();
        adapter.setListTugas(tugasArrayList);
    }
}