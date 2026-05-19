package com.example.speechkids_ai.ui.parent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R
import com.example.speechkids_ai.games.FullScreenGameActivity

class ParentExercisesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent_exercises, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exercise1 = view.findViewById<View>(R.id.exerciseCard1)
        val exercise2 = view.findViewById<View>(R.id.exerciseCard2)
        val exercise3 = view.findViewById<View>(R.id.exerciseCard3)

        val launchGame: (String) -> Unit = { gameName ->
            Toast.makeText(context, "Запуск игры: $gameName", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), FullScreenGameActivity::class.java)
            startActivity(intent)
        }

        exercise1.setOnClickListener { launchGame("Рычащие джунгли") }
        exercise2.setOnClickListener { launchGame("Шипящие змейки") }
        exercise3.setOnClickListener { launchGame("Ловкий язычок") }
    }
}
