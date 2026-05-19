package com.example.speechkids_ai.ui.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.speechkids_ai.R
import com.example.speechkids_ai.model.UserRole
import com.example.speechkids_ai.utils.RoleManager
import com.example.speechkids_ai.viewmodel.ParentViewModel
import com.google.android.material.button.MaterialButton

class ParentChildrenFragment : Fragment() {
    private val parentViewModel: ParentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent_children, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.openGameButton).setOnClickListener {
            // Set role to Child Mode
            RoleManager.setRole(UserRole.CHILD)
            Toast.makeText(context, "Включен Детский Режим", Toast.LENGTH_SHORT).show()
            // Navigate to overview showing Kid layout
            findNavController().navigate(R.id.parentOverviewFragment)
        }

        // Connect other actions for static mock profiles
        val switchButtons = listOf<View>(
            view.findViewById(R.id.openGameButton) // placeholder
        )
    }
}
