package com.cibertec.meditrackapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicalRecordAdapter(
    private val onItemClicked: (MedicalRecord) -> Unit
) : RecyclerView.Adapter<MedicalRecordAdapter.MedicalRecordViewHolder>() {

    private val records = mutableListOf<MedicalRecord>()

    fun submitList(newList: List<MedicalRecord>) {
        records.clear()
        records.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalRecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medical_record, parent, false)
        return MedicalRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicalRecordViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int = records.size

    inner class MedicalRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAge: TextView = itemView.findViewById(R.id.tvAge)
        private val tvHeight: TextView = itemView.findViewById(R.id.tvHeight)
        private val tvBloodPressure: TextView = itemView.findViewById(R.id.tvBloodPressure)
        private val tvComment: TextView = itemView.findViewById(R.id.tvComment)

        fun bind(record: MedicalRecord) {
            tvDate.text = record.date
            tvTime.text = record.time
            tvName.text = record.fullName
            tvAge.text = itemView.context.getString(R.string.label_age_with_value, record.age)
            tvHeight.text = itemView.context.getString(R.string.label_height_with_value, record.height)
            tvBloodPressure.text = itemView.context.getString(
                R.string.label_blood_pressure_with_value,
                record.bloodPressure
            )
            val commentText = record.comment.ifBlank {
                itemView.context.getString(R.string.label_no_comment)
            }
            tvComment.text = itemView.context.getString(
                R.string.label_comments_with_value,
                commentText
            )

            itemView.setOnClickListener { onItemClicked(record) }
        }
    }
}
