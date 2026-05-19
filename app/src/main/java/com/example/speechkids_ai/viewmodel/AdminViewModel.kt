package com.example.speechkids_ai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminViewModel : ViewModel() {
    private val _userCount = MutableLiveData(42)
    val userCount: LiveData<Int> = _userCount

    private val _analyticsData = MutableLiveData<Map<String, Int>>(emptyMap())
    val analyticsData: LiveData<Map<String, Int>> = _analyticsData

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        _analyticsData.value = mapOf(
            "parents" to 28,
            "therapists" to 12,
            "children" to 45,
            "sessions" to 234
        )
    }
}

