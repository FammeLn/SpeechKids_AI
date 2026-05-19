package com.example.speechkids_ai.ui.parent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.speechkids_ai.R
import com.example.speechkids_ai.games.FullScreenGameActivity
import com.example.speechkids_ai.model.UserRole
import com.example.speechkids_ai.utils.RoleManager
import com.example.speechkids_ai.viewmodel.ParentViewModel
import com.google.android.material.chip.Chip

class ParentOverviewFragment : Fragment() {
    private val parentViewModel: ParentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val kidModeChip = view.findViewById<Chip>(R.id.kidModeChip)
        val activeChildCard = view.findViewById<View>(R.id.activeChildCard)
        val activeChildNameText = view.findViewById<TextView>(R.id.activeChildNameText)
        val activeChildAdaptationText = view.findViewById<TextView>(R.id.activeChildAdaptationText)
        val startRecommendedButton = view.findViewById<Button>(R.id.startRecommendedButton)

        // Switch styles if we are in child mode
        val isChildMode = RoleManager.getRole() == UserRole.CHILD
        if (isChildMode) {
            kidModeChip.text = "Взрослый режим"
            kidModeChip.setChipBackgroundColorResource(android.R.color.holo_green_light)
            kidModeChip.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            view.setBackgroundColor(resources.getColor(R.color.child_bg, null))
        } else {
            kidModeChip.text = "Детский режим"
            kidModeChip.setChipBackgroundColorResource(android.R.color.holo_red_light)
            kidModeChip.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            view.setBackgroundColor(resources.getColor(R.color.surface, null))
        }

        // Toggle kid mode
        kidModeChip.setOnClickListener {
            if (RoleManager.getRole() == UserRole.CHILD) {
                RoleManager.setRole(UserRole.PARENT)
                Toast.makeText(context, "Взрослый режим активирован", Toast.LENGTH_SHORT).show()
            } else {
                RoleManager.setRole(UserRole.CHILD)
                Toast.makeText(context, "Детский режим активирован", Toast.LENGTH_SHORT).show()
            }
            // Refresh fragment view
            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }

        // Bind active child info from viewmodel
        parentViewModel.children.observe(viewLifecycleOwner) { children ->
            if (children.isNotEmpty()) {
                val primaryChild = children[0]
                activeChildNameText.text = "${primaryChild.name}, ${primaryChild.age} лет"
                activeChildAdaptationText.text = "Модель адаптирована на ${primaryChild.progress}%"
            } else {
                activeChildNameText.text = "Лев, 5 лет"
                activeChildAdaptationText.text = "Модель адаптирована на 85%"
            }
        }

        // Child card action: show quick children switch info
        activeChildCard.setOnClickListener {
            val children = parentViewModel.children.value ?: emptyList()
            if (children.size > 1) {
                val nextChild = children[1]
                Toast.makeText(context, "Переключено на профиль: ${nextChild.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "В системе только один ребенок. Создайте второго во вкладке 'Дети'.", Toast.LENGTH_SHORT).show()
            }
        }

        // Launch Game
        startRecommendedButton.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenGameActivity::class.java)
            startActivity(intent)
        }
    }
}
