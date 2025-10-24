package com.cibertec.meditrackapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.meditrackapp.R
import com.cibertec.meditrackapp.data.MedicalDbHelper
import kotlin.math.roundToInt

class MedicalRecordDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: MedicalDbHelper
    private var recordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_record_detail)

        dbHelper = MedicalDbHelper(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val tvDate: TextView = findViewById(R.id.tvDetailDate)
        val tvTime: TextView = findViewById(R.id.tvDetailTime)
        val tvName: TextView = findViewById(R.id.tvDetailName)
        val tvAge: TextView = findViewById(R.id.tvDetailAge)
        val tvHeight: TextView = findViewById(R.id.tvDetailHeight)
        val tvWeight: TextView = findViewById(R.id.tvDetailWeight)
        val tvBloodPressure: TextView = findViewById(R.id.tvDetailBloodPressure)
        val tvComment: TextView = findViewById(R.id.tvDetailComment)
        val tvBmi: TextView = findViewById(R.id.tvDetailBmi)
        val btnDelete: Button = findViewById(R.id.btnDeleteRecord)

        btnBack.setOnClickListener { finish() }

        recordId = intent.getLongExtra(EXTRA_RECORD_ID, -1L)
        if (recordId == -1L) {
            Toast.makeText(this, R.string.message_record_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val record = dbHelper.getRecordById(recordId)
        if (record == null) {
            Toast.makeText(this, R.string.message_record_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvDate.text = record.date
        tvTime.text = record.time
        tvName.text = record.fullName
        tvAge.text = getString(R.string.label_age_with_value, record.age)
        tvHeight.text = getString(R.string.label_height_with_value, record.height)
        tvWeight.text = getString(R.string.label_weight_with_value, record.weight)
        tvBloodPressure.text = getString(R.string.label_blood_pressure_with_value, record.bloodPressure)
        val commentText = record.comment.ifBlank { getString(R.string.label_no_comment) }
        tvComment.text = getString(R.string.label_comments_with_value, commentText)
        val bmiRounded = (record.bmi * 10).roundToInt() / 10.0
        val bmiDescription = getString(
            R.string.label_bmi_value,
            bmiRounded,
            record.bmiClassification()
        )
        tvBmi.text = bmiDescription

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete)
                .setMessage(R.string.dialog_message_delete)
                .setPositiveButton(R.string.dialog_button_delete) { dialog, _ ->
                    dialog.dismiss()
                    deleteRecord()
                }
                .setNegativeButton(R.string.dialog_button_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun deleteRecord() {
        val deleted = dbHelper.deleteRecord(recordId)
        if (deleted) {
            Toast.makeText(this, R.string.message_record_deleted, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, R.string.message_error_deleting, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_RECORD_ID = "extra_record_id"
    }
}
