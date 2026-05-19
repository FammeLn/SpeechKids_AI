package com.example.speechkids_ai.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.speechkids_ai.R
import com.example.speechkids_ai.model.Child
import com.example.speechkids_ai.model.UserRole
import com.example.speechkids_ai.utils.RoleManager
import com.example.speechkids_ai.viewmodel.ParentViewModel
import com.google.android.material.card.MaterialCardView

class RoleSelectionFragment : Fragment() {
    private val parentViewModel: ParentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_role_selection, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roleSelectionContainer = view.findViewById<LinearLayout>(R.id.roleSelectionContainer)
        val profileSelectionContainer = view.findViewById<LinearLayout>(R.id.profileSelectionContainer)
        val childrenListContainer = view.findViewById<LinearLayout>(R.id.childrenListContainer)

        // Role Cards Selection
        view.findViewById<View>(R.id.parentRoleCard).setOnClickListener {
            roleSelectionContainer.visibility = View.GONE
            profileSelectionContainer.visibility = View.VISIBLE
            loadChildProfiles(childrenListContainer)
        }

        view.findViewById<View>(R.id.therapistRoleCard).setOnClickListener {
            RoleManager.setRole(UserRole.THERAPIST)
            findNavController().navigate(R.id.therapistOverviewFragment)
        }

        view.findViewById<View>(R.id.adminRoleCard).setOnClickListener {
            RoleManager.setRole(UserRole.ADMIN)
            findNavController().navigate(R.id.adminOverviewFragment)
        }

        // Parent Profile Clicked (Adult Mode)
        view.findViewById<View>(R.id.parentProfileCard).setOnClickListener {
            RoleManager.setRole(UserRole.PARENT)
            findNavController().navigate(R.id.parentOverviewFragment)
        }

        // Add Child profile
        val nameInput = view.findViewById<EditText>(R.id.newChildNameInput)
        val ageInput = view.findViewById<EditText>(R.id.newChildAgeInput)
        view.findViewById<Button>(R.id.createChildButton).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val ageStr = ageInput.text.toString().trim()
            if (name.isNotEmpty() && ageStr.isNotEmpty()) {
                val age = ageStr.toIntOrNull() ?: 5
                val newChild = Child(
                    id = "child_${System.currentTimeMillis()}",
                    name = name,
                    age = age,
                    progress = 0,
                    streak = 0
                )
                parentViewModel.addChild(newChild)
                nameInput.text.clear()
                ageInput.text.clear()
                Toast.makeText(context, "Профиль ребенка добавлен!", Toast.LENGTH_SHORT).show()
                loadChildProfiles(childrenListContainer)
            } else {
                Toast.makeText(context, "Заполните имя и возраст", Toast.LENGTH_SHORT).show()
            }
        }

        // Back button to choose roles
        view.findViewById<Button>(R.id.backToRoleButton).setOnClickListener {
            profileSelectionContainer.visibility = View.GONE
            roleSelectionContainer.visibility = View.VISIBLE
        }
    }

    private fun loadChildProfiles(container: LinearLayout) {
        container.removeAllViews()
        val children = parentViewModel.children.value ?: emptyList()

        for (child in children) {
            val childCard = layoutInflater.inflate(R.layout.item_role_child_profile, container, false)
            childCard.findViewById<TextView>(R.id.childName).text = child.name
            childCard.findViewById<TextView>(R.id.childAge).text = "${child.age} лет • Прогресс: ${child.progress}%"
            
            childCard.setOnClickListener {
                RoleManager.setRole(UserRole.CHILD)
                Toast.makeText(context, "Детский режим для ребенка: ${child.name}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.parentOverviewFragment)
            }
            container.addView(childCard)
        }
    }
}
