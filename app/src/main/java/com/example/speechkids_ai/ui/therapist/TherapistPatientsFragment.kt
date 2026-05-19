package com.example.speechkids_ai.ui.therapist

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.speechkids_ai.R
import com.example.speechkids_ai.model.Patient
import com.example.speechkids_ai.model.PhonemeStatus
import com.example.speechkids_ai.viewmodel.TherapistViewModel
import com.google.android.material.chip.Chip

class TherapistPatientsFragment : Fragment() {
    private val viewModel: TherapistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_therapist_patients, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patientsListScreen = view.findViewById<LinearLayout>(R.id.patientsListScreen)
        val patientDetailContainer = view.findViewById<ViewGroup>(R.id.patientDetailContainer)
        val patientsContainer = view.findViewById<LinearLayout>(R.id.patientsContainer)

        viewModel.patients.observe(viewLifecycleOwner) { patients ->
            patientsContainer.removeAllViews()
            for (patient in patients) {
                val card = layoutInflater.inflate(R.layout.item_therapist_patient, patientsContainer, false)
                card.findViewById<TextView>(R.id.patientNameText).text = patient.name
                card.findViewById<TextView>(R.id.patientDiagnosisText).text = patient.diagnosis
                card.findViewById<TextView>(R.id.patientProgressText).text = "Успешность: ${patient.progress}%"

                card.setOnClickListener {
                    patientsListScreen.visibility = View.GONE
                    patientDetailContainer.visibility = View.VISIBLE
                    showPatientDetail(patientDetailContainer, patientsListScreen, patient)
                }

                patientsContainer.addView(card)
            }
        }
    }

    private fun showPatientDetail(container: ViewGroup, listScreen: View, patient: Patient) {
        container.removeAllViews()
        val detailView = layoutInflater.inflate(R.layout.fragment_therapist_patient_detail, container, false)

        detailView.findViewById<TextView>(R.id.detailPatientName).text = patient.name
        detailView.findViewById<TextView>(R.id.detailPatientDiagnosis).text = "Диагноз: ${patient.diagnosis}"
        detailView.findViewById<TextView>(R.id.detailPatientProgress).text = "Общий прогресс: ${patient.progress}%"

        // Setup Back Button
        detailView.findViewById<Button>(R.id.backToPatientsButton).setOnClickListener {
            container.removeAllViews()
            container.visibility = View.GONE
            listScreen.visibility = View.VISIBLE
        }

        // Setup Grid
        val grid = detailView.findViewById<GridLayout>(R.id.phonemeGridLayout)
        val dynamicsCard = detailView.findViewById<View>(R.id.phonemeDynamicsCard)
        val selectedPhonemeTitle = detailView.findViewById<TextView>(R.id.selectedPhonemeTitle)
        val selectedPhonemeStats = detailView.findViewById<TextView>(R.id.selectedPhonemeStats)

        // Load mock phonemes
        viewModel.loadPhonemicMap(patient.id)
        val phonemes = viewModel.phonemicMap.value?.phonemes ?: emptyList()

        grid.removeAllViews()
        for (phoneme in phonemes) {
            val cell = TextView(context).apply {
                text = phoneme.phoneme
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(resources.getColor(android.R.color.white, null))
                gravity = Gravity.CENTER
                
                // Color by status
                val bgColor = when (phoneme.status) {
                    "good" -> R.color.success
                    "fair" -> R.color.warning
                    else -> R.color.danger
                }
                setBackgroundColor(resources.getColor(bgColor, null))

                // Set layout margins and padding
                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 110 // height in pixels
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }
                layoutParams = params
            }

            cell.setOnClickListener {
                dynamicsCard.visibility = View.VISIBLE
                selectedPhonemeTitle.text = "Фонема [${phoneme.phoneme.uppercase()}]"
                selectedPhonemeStats.text = "Точность: ${phoneme.accuracy.toInt()}% (${getRecommendation(phoneme.status)})"
                
                val statsColor = when (phoneme.status) {
                    "good" -> R.color.success
                    "fair" -> R.color.warning
                    else -> R.color.danger
                }
                selectedPhonemeStats.setTextColor(resources.getColor(statsColor, null))
            }

            grid.addView(cell)
        }

        // Setup Assign homework action
        val assignButton = detailView.findViewById<Button>(R.id.assignExerciseButton)
        assignButton.setOnClickListener {
            val chip1 = detailView.findViewById<Chip>(R.id.chipEx1)
            val chip2 = detailView.findViewById<Chip>(R.id.chipEx2)
            val selectedGame = when {
                chip1.isChecked -> "Рычащие джунгли"
                chip2.isChecked -> "Шипящие змейки"
                else -> "Ловкий язычок"
            }
            Toast.makeText(context, "Упражнение '$selectedGame' успешно назначено ребенку ${patient.name}!", Toast.LENGTH_LONG).show()
        }

        // Export PDF action
        detailView.findViewById<Button>(R.id.exportReportButton).setOnClickListener {
            Toast.makeText(context, "Генерация отчета PDF для ${patient.name}...", Toast.LENGTH_SHORT).show()
            detailView.postDelayed({
                Toast.makeText(context, "Отчет экспортирован в файл: SpeechKids_Report_${patient.name}.pdf", Toast.LENGTH_LONG).show()
            }, 1500)
        }

        container.addView(detailView)
    }

    private fun getRecommendation(status: String): String {
        return when (status) {
            "good" -> "Отличный результат"
            "fair" -> "Хорошо, продолжайте тренировки"
            else -> "Требует активной проработки"
        }
    }
}
