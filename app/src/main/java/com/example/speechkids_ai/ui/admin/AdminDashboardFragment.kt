package com.example.speechkids_ai.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.speechkids_ai.R
import com.example.speechkids_ai.viewmodel.AdminViewModel

class AdminDashboardFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var analyticsText: TextView
    private lateinit var userCountText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        analyticsText = view.findViewById(R.id.analyticsText)
        userCountText = view.findViewById(R.id.userCountText)
        observeAnalytics()
    }

    private fun observeAnalytics() {
        viewModel.analyticsData.observe(viewLifecycleOwner) { data ->
            analyticsText.text = "Аналитика: ${data.size} метрик"
        }

        viewModel.userCount.observe(viewLifecycleOwner) { count ->
            userCountText.text = "Всего пользователей: $count"
        }
    }
}


