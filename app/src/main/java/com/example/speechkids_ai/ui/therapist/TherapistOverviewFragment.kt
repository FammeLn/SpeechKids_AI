package com.example.speechkids_ai.ui.therapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.speechkids_ai.R

class TherapistOverviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_therapist_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.therapistLogoutButton).setOnClickListener {
            Toast.makeText(context, "Выход из профиля логопеда...", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.authFragment)
        }
    }
}
