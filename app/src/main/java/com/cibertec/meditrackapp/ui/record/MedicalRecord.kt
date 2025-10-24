package com.cibertec.meditrackapp.ui.record

data class MedicalRecord(
    val id: Long = 0,
    val fullName: String,
    val age: Int,
    val weight: Double,
    val height: Double,
    val bloodPressure: String,
    val comment: String,
    val bmi: Double,
    val date: String,
    val time: String
) {
    fun bmiClassification(): String = when {
        bmi < 18.5 -> "Bajo peso"
        bmi < 25.0 -> "Peso normal"
        bmi < 30.0 -> "Sobrepeso"
        else -> "Obesidad"
    }
}
