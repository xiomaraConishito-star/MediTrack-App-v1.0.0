package com.cibertec.meditrackapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class AddMedicalRecordActivity : AppCompatActivity() {

    private lateinit var dbHelper: MedicalDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medical_record)

        dbHelper = MedicalDbHelper(this)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val inputFullName: EditText = findViewById(R.id.inputFullName)
        val inputAge: EditText = findViewById(R.id.inputAge)
        val inputWeight: EditText = findViewById(R.id.inputWeight)
        val inputHeight: EditText = findViewById(R.id.inputHeight)
        val inputBloodPressure: EditText = findViewById(R.id.inputBloodPressure)
        val inputComment: EditText = findViewById(R.id.inputComment)
        val btnSave: Button = findViewById(R.id.btnSave)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val fullName = inputFullName.text.toString().trim()
            val ageText = inputAge.text.toString().trim()
            val weightText = inputWeight.text.toString().trim()
            val heightText = inputHeight.text.toString().trim()
            val bloodPressure = inputBloodPressure.text.toString().trim()
            val comment = inputComment.text.toString().trim()

            if (fullName.isEmpty() || ageText.isEmpty() || weightText.isEmpty() ||
                heightText.isEmpty() || bloodPressure.isEmpty()
            ) {
                Toast.makeText(this, R.string.message_complete_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            val weight = weightText.toDoubleOrNull()
            val height = heightText.toDoubleOrNull()

            if (age == null || weight == null || height == null || height <= 0.0) {
                Toast.makeText(this, R.string.message_invalid_numbers, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val normalizedHeight = if (height > 3.0) {
                // Permite ingresar altura en centímetros convirtiéndola a metros
                height / 100.0
            } else {
                height
            }

            val bmi = weight / normalizedHeight.pow(2.0)
            val now = LocalDateTime.now()
            val date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val time = now.format(DateTimeFormatter.ofPattern("HH:mm"))

            val record = MedicalRecord(
                fullName = fullName,
                age = age,
                weight = weight,
                height = normalizedHeight,
                bloodPressure = bloodPressure,
                comment = comment,
                bmi = bmi,
                date = date,
                time = time
            )

            val insertedId = dbHelper.insertRecord(record)
            if (insertedId != -1L) {
                showSuccessDialog()
            } else {
                Toast.makeText(this, R.string.message_error_saving, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_success)
            .setMessage(R.string.dialog_message_success)
            .setPositiveButton(R.string.dialog_button_understood) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
