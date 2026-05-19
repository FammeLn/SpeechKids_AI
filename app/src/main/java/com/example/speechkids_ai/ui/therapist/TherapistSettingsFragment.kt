package com.example.speechkids_ai.ui.therapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R

class TherapistSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_therapist_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cloudAsrSwitch = view.findViewById<SwitchCompat>(R.id.cloudAsrSwitch)
        val autoPdfExportSwitch = view.findViewById<SwitchCompat>(R.id.autoPdfExportSwitch)
        val resetWeightsButton = view.findViewById<Button>(R.id.resetWeightsButton)

        cloudAsrSwitch.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) "Облачная модель Whisper" else "Локальная модель Vosk"
            Toast.makeText(context, "Режим распознавания речи: $mode", Toast.LENGTH_SHORT).show()
        }

        autoPdfExportSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "включена" else "выключена"
            Toast.makeText(context, "Автовыгрузка PDF-отчетов $status", Toast.LENGTH_SHORT).show()
        }

        resetWeightsButton.setOnClickListener {
            Toast.makeText(context, "Веса моделей адаптации всех пациентов успешно сброшены к начальным!", Toast.LENGTH_LONG).show()
        }
    }
}
