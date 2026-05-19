package com.example.speechkids_ai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.speechkids_ai.model.Patient
import com.example.speechkids_ai.model.PhonemeStatus
import com.example.speechkids_ai.model.PhonemicMap

class TherapistViewModel : ViewModel() {
    private val _patients = MutableLiveData<List<Patient>>(emptyList())
    val patients: LiveData<List<Patient>> = _patients

    private val _phonemicMap = MutableLiveData<PhonemicMap?>(null)
    val phonemicMap: LiveData<PhonemicMap?> = _phonemicMap

    init {
        loadPatients()
    }

    private fun loadPatients() {
        _patients.value = listOf(
            Patient(
                id = "patient_1",
                name = "Максим",
                diagnosis = "Фонетическое нарушение",
                progress = 70
            ),
            Patient(
                id = "patient_2",
                name = "Арина",
                diagnosis = "Дислалия",
                progress = 50
            )
        )
    }

    fun loadPhonemicMap(patientId: String) {
        val phonemes = generateMockPhonemes()
        _phonemicMap.value = PhonemicMap(patientId, phonemes)
    }

    private fun generateMockPhonemes(): List<PhonemeStatus> {
        return listOf(
            PhonemeStatus("а", 95f, System.currentTimeMillis(), "good"),
            PhonemeStatus("б", 88f, System.currentTimeMillis(), "good"),
            PhonemeStatus("в", 72f, System.currentTimeMillis(), "fair"),
            PhonemeStatus("г", 65f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("д", 91f, System.currentTimeMillis(), "good"),
            PhonemeStatus("е", 87f, System.currentTimeMillis(), "good"),
            PhonemeStatus("ё", 75f, System.currentTimeMillis(), "fair"),
            PhonemeStatus("ж", 45f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("з", 58f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("и", 93f, System.currentTimeMillis(), "good"),
            PhonemeStatus("й", 68f, System.currentTimeMillis(), "fair"),
            PhonemeStatus("к", 82f, System.currentTimeMillis(), "good"),
            PhonemeStatus("л", 55f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("м", 89f, System.currentTimeMillis(), "good"),
            PhonemeStatus("н", 85f, System.currentTimeMillis(), "good"),
            PhonemeStatus("о", 92f, System.currentTimeMillis(), "good"),
            PhonemeStatus("п", 79f, System.currentTimeMillis(), "fair"),
            PhonemeStatus("р", 40f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("с", 62f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("т", 90f, System.currentTimeMillis(), "good"),
            PhonemeStatus("у", 94f, System.currentTimeMillis(), "good"),
            PhonemeStatus("ф", 71f, System.currentTimeMillis(), "fair"),
            PhonemeStatus("х", 68f, System.currentTimeMillis(), "fair"),
            PhonemeStatus("ц", 48f, System.currentTimeMillis(), "needs_work"),
            PhonemeStatus("ч", 52f, System.currentTimeMillis(), "needs_work")
        )
    }
}

