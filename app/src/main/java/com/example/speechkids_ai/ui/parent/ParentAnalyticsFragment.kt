package com.example.speechkids_ai.ui.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R

class ParentAnalyticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent_analytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val downloadReportButton = view.findViewById<Button>(R.id.downloadReportButton)
        downloadReportButton.setOnClickListener {
            Toast.makeText(context, "Отчет за Октябрь успешно скачан в папку Загрузки!", Toast.LENGTH_LONG).show()
        }
    }
}
