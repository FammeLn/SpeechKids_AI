package com.example.speechkids_ai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _gameState = MutableLiveData("ready")
    val gameState: LiveData<String> = _gameState

    private val _xpReward = MutableLiveData(0)
    val xpReward: LiveData<Int> = _xpReward

    private val _wordCount = MutableLiveData(0)
    val wordCount: LiveData<Int> = _wordCount

    fun startRecording() {
        _gameState.value = "recording"
    }

    fun stopRecording() {
        _gameState.value = "processing"
    }

    fun submitResult(accuracy: Float) {
        val xp = (accuracy * 10).toInt()
        _xpReward.value = xp
        _gameState.value = "result"
    }

    fun updateWordCount(count: Int) {
        _wordCount.value = count
    }

    fun nextRound() {
        _gameState.value = "ready"
        _xpReward.value = 0
        _wordCount.value = 0
    }
}

