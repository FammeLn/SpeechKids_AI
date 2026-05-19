package com.example.speechkids_ai.games

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R

class GameHubFragment : Fragment() {

    private var currentStep = 0
    private var starsCount = 0
    private val targetWords = listOf("Р-Р-РЫБА", "Р-Р-РАДУГА", "ТИГР-Р-Р")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_game_hub, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val welcomeContainer = view.findViewById<LinearLayout>(R.id.gameWelcomeContainer)
        val playingContainer = view.findViewById<LinearLayout>(R.id.gamePlayingContainer)
        val resultContainer = view.findViewById<LinearLayout>(R.id.gameResultContainer)

        val playButton = view.findViewById<Button>(R.id.playButton)
        val micRecordButton = view.findViewById<Button>(R.id.micRecordButton)
        val finishGameButton = view.findViewById<Button>(R.id.finishGameButton)

        val targetWordText = view.findViewById<TextView>(R.id.targetWordText)
        val recordingStatusText = view.findViewById<TextView>(R.id.recordingStatusText)
        val gameStarsCountText = view.findViewById<TextView>(R.id.gameStarsCountText)
        val gameProgressBar = view.findViewById<ProgressBar>(R.id.gameProgressBar)

        // START GAME ACTION
        playButton.setOnClickListener {
            welcomeContainer.visibility = View.GONE
            playingContainer.visibility = View.VISIBLE
            currentStep = 0
            starsCount = 0
            updateStep(targetWordText, gameStarsCountText, gameProgressBar)
        }

        // MIC RECORDING SIMULATOR ACTION
        micRecordButton.setOnClickListener {
            micRecordButton.isEnabled = false
            micRecordButton.text = "⏳"
            recordingStatusText.text = "Слушаю... Говори!"
            Toast.makeText(context, "ИИ распознает звук...", Toast.LENGTH_SHORT).show()

            // Simulating speech recognition latency
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAdded) {
                    micRecordButton.isEnabled = true
                    micRecordButton.text = "🎙️"
                    recordingStatusText.text = "Отлично произнесено!"
                    starsCount++
                    currentStep++

                    if (currentStep >= targetWords.size) {
                        // Game Over: Switch to Results
                        playingContainer.visibility = View.GONE
                        resultContainer.visibility = View.VISIBLE
                    } else {
                        // Next Word
                        updateStep(targetWordText, gameStarsCountText, gameProgressBar)
                    }
                }
            }, 1800)
        }

        // FINISH GAME ACTION
        finishGameButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun updateStep(wordView: TextView, starsView: TextView, progressBar: ProgressBar) {
        wordView.text = targetWords[currentStep]
        starsView.text = "⭐ $starsCount"
        progressBar.progress = currentStep
    }
}
