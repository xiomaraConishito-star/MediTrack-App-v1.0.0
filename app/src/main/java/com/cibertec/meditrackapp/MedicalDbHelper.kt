package com.cibertec.meditrackapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.math.pow

class MedicalDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_MEDICAL_RECORD (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FULL_NAME TEXT NOT NULL,
                $COLUMN_AGE INTEGER NOT NULL,
                $COLUMN_WEIGHT REAL NOT NULL,
                $COLUMN_HEIGHT REAL NOT NULL,
                $COLUMN_BLOOD_PRESSURE TEXT NOT NULL,
                $COLUMN_COMMENT TEXT,
                $COLUMN_BMI REAL NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICAL_RECORD")
        onCreate(db)
    }

    fun insertRecord(record: MedicalRecord): Long {
        val normalizedHeight = normalizeHeight(record.height)
        val bmi = calculateBmi(record.weight, normalizedHeight)

        val values = ContentValues().apply {
            put(COLUMN_FULL_NAME, record.fullName)
            put(COLUMN_AGE, record.age)
            put(COLUMN_WEIGHT, record.weight)
            put(COLUMN_HEIGHT, normalizedHeight)
            put(COLUMN_BLOOD_PRESSURE, record.bloodPressure)
            put(COLUMN_COMMENT, record.comment)
            put(COLUMN_BMI, bmi)
            put(COLUMN_DATE, record.date)
            put(COLUMN_TIME, record.time)
        }
        return writableDatabase.use { db ->
            db.insert(TABLE_MEDICAL_RECORD, null, values)
        }
    }

    fun getAllRecords(): List<MedicalRecord> {
        val records = mutableListOf<MedicalRecord>()
        readableDatabase.use { db ->
            db.query(
                TABLE_MEDICAL_RECORD,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_ID DESC"
            ).use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
                val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)
                val ageIndex = cursor.getColumnIndexOrThrow(COLUMN_AGE)
                val weightIndex = cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)
                val heightIndex = cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)
                val bloodPressureIndex = cursor.getColumnIndexOrThrow(COLUMN_BLOOD_PRESSURE)
                val commentIndex = cursor.getColumnIndexOrThrow(COLUMN_COMMENT)
                val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
                val timeIndex = cursor.getColumnIndexOrThrow(COLUMN_TIME)

                while (cursor.moveToNext()) {
                    records.add(
                        cursor.toMedicalRecord(
                            idIndex = idIndex,
                            nameIndex = nameIndex,
                            ageIndex = ageIndex,
                            weightIndex = weightIndex,
                            heightIndex = heightIndex,
                            bloodPressureIndex = bloodPressureIndex,
                            commentIndex = commentIndex,
                            dateIndex = dateIndex,
                            timeIndex = timeIndex
                        )
                    )
                }
            }
        }
        return records
    }

    fun getRecordById(id: Long): MedicalRecord? {
        readableDatabase.use { db ->
            db.query(
                TABLE_MEDICAL_RECORD,
                null,
                "$COLUMN_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
                    val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)
                    val ageIndex = cursor.getColumnIndexOrThrow(COLUMN_AGE)
                    val weightIndex = cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)
                    val heightIndex = cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)
                    val bloodPressureIndex = cursor.getColumnIndexOrThrow(COLUMN_BLOOD_PRESSURE)
                    val commentIndex = cursor.getColumnIndexOrThrow(COLUMN_COMMENT)
                    val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
                    val timeIndex = cursor.getColumnIndexOrThrow(COLUMN_TIME)

                    return cursor.toMedicalRecord(
                        idIndex = idIndex,
                        nameIndex = nameIndex,
                        ageIndex = ageIndex,
                        weightIndex = weightIndex,
                        heightIndex = heightIndex,
                        bloodPressureIndex = bloodPressureIndex,
                        commentIndex = commentIndex,
                        dateIndex = dateIndex,
                        timeIndex = timeIndex
                    )
                }
            }
        }
        return null
    }

    fun deleteRecord(id: Long): Boolean {
        val deletedRows = writableDatabase.use { db ->
            db.delete(TABLE_MEDICAL_RECORD, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }
        return deletedRows > 0
    }

    companion object {
        private const val DATABASE_NAME = "medical_records.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_MEDICAL_RECORD = "medical_record"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_AGE = "age"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_BLOOD_PRESSURE = "blood_pressure"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_BMI = "bmi"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
    }

    private fun normalizeHeight(rawHeight: Double): Double =
        if (rawHeight > 3.0) rawHeight / 100.0 else rawHeight

    private fun calculateBmi(weight: Double, heightMeters: Double): Double =
        if (heightMeters > 0.0) weight / heightMeters.pow(2.0) else 0.0

    private fun android.database.Cursor.toMedicalRecord(
        idIndex: Int,
        nameIndex: Int,
        ageIndex: Int,
        weightIndex: Int,
        heightIndex: Int,
        bloodPressureIndex: Int,
        commentIndex: Int,
        dateIndex: Int,
        timeIndex: Int
    ): MedicalRecord {
        val weight = getDouble(weightIndex)
        val normalizedHeight = normalizeHeight(getDouble(heightIndex))
        val bmi = calculateBmi(weight, normalizedHeight)
        val commentValue = getString(commentIndex) ?: ""

        return MedicalRecord(
            id = getLong(idIndex),
            fullName = getString(nameIndex),
            age = getInt(ageIndex),
            weight = weight,
            height = normalizedHeight,
            bloodPressure = getString(bloodPressureIndex),
            comment = commentValue,
            bmi = bmi,
            date = getString(dateIndex),
            time = getString(timeIndex)
        )
    }
}
