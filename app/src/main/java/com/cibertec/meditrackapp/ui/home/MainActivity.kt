package com.cibertec.meditrackapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.meditrackapp.R
import com.cibertec.meditrackapp.data.MedicalDbHelper
import com.cibertec.meditrackapp.ui.detail.MedicalRecordAdapter
import com.cibertec.meditrackapp.ui.detail.MedicalRecordDetailActivity
import com.cibertec.meditrackapp.ui.record.AddMedicalRecordActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MedicalDbHelper
    private lateinit var adapter: MedicalRecordAdapter
    private lateinit var emptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = MedicalDbHelper(this)
        adapter = MedicalRecordAdapter { record ->
            val intent = Intent(this, MedicalRecordDetailActivity::class.java).apply {
                putExtra(MedicalRecordDetailActivity.Companion.EXTRA_RECORD_ID, record.id)
            }
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerMedicalRecords)
        emptyState = findViewById(R.id.tvEmptyState)
        val btnAdd: View = findViewById(R.id.btnAddRecord)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddMedicalRecordActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }

    private fun loadRecords() {
        val records = dbHelper.getAllRecords()
        adapter.submitList(records)
        emptyState.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
    }
}