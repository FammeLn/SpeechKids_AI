package com.example.speechkids_ai.ui.therapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R

class TherapistSessionsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_therapist_sessions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.playVoiceButton1).setOnClickListener {
            Toast.makeText(context, "Воспроизведение записи Максима: 'Карта, рыба, трава...'", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.playVoiceButton2).setOnClickListener {
            Toast.makeText(context, "Воспроизведение записи Арины: 'Шапка, соска...'", Toast.LENGTH_SHORT).show()
        }
    }
}
