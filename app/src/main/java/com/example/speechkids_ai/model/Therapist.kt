package com.example.speechkids_ai.model

data class Patient(
    val id: String,
    val name: String,
    val diagnosis: String? = null,
    val progress: Int = 0,
    val avatar: String? = null,
    val notes: String = ""
)

data class Report(
    val id: String,
    val patientId: String,
    val date: Long,
    val content: String,
    val type: String,  // phonemic, fluency, prosody, etc.
    val pdfUrl: String? = null
)

data class PhonemicMap(
    val patientId: String,
    val phonemes: List<PhonemeStatus>
)

data class PhonemeStatus(
    val phoneme: String,
    val accuracy: Float,  // 0-100
    val lastPracticed: Long? = null,
    val status: String  // good, fair, needs_work
)

