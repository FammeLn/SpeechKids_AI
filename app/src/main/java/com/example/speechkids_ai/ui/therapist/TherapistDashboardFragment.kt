package com.example.speechkids_ai.ui.therapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.speechkids_ai.R
import com.example.speechkids_ai.viewmodel.TherapistViewModel

class TherapistDashboardFragment : Fragment() {
    private val viewModel: TherapistViewModel by viewModels()
    private lateinit var patientCountText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_therapist_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        patientCountText = view.findViewById(R.id.patientCountText)
        observePatients()
    }

    private fun observePatients() {
        viewModel.patients.observe(viewLifecycleOwner) { patients ->
            patientCountText.text = "Пациентов: ${patients.size}"
        }
    }
}


