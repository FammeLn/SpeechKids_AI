package com.example.speechkids_ai.ui.therapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R
import com.google.android.material.chip.Chip

class TherapistReportsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_therapist_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chipPatient1 = view.findViewById<Chip>(R.id.chipPatient1)
        val chipMonthOct = view.findViewById<Chip>(R.id.chipMonthOct)
        val commentInput = view.findViewById<EditText>(R.id.reportCommentInput)
        val generateButton = view.findViewById<Button>(R.id.generateReportButton)

        generateButton.setOnClickListener {
            val patient = if (chipPatient1.isChecked) "Максим" else "Арина"
            val month = if (chipMonthOct.isChecked) "Октябрь" else "Ноябрь"
            val comment = commentInput.text.toString().trim()

            Toast.makeText(context, "Идет формирование PDF отчета для пациента $patient за $month...", Toast.LENGTH_SHORT).show()
            
            generateButton.postDelayed({
                Toast.makeText(context, "PDF отчет за $month успешно создан!", Toast.LENGTH_LONG).show()
                commentInput.text.clear()
            }, 2000)
        }
    }
}
