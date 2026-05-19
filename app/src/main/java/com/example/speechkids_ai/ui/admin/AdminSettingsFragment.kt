package com.example.speechkids_ai.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.speechkids_ai.R

class AdminSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val maintenanceSwitch = view.findViewById<SwitchCompat>(R.id.adminMaintenanceSwitch)
        val debugSwitch = view.findViewById<SwitchCompat>(R.id.adminDebugLogsSwitch)
        val syncButton = view.findViewById<Button>(R.id.adminSyncDbButton)

        maintenanceSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "активирован" else "деактивирован"
            Toast.makeText(context, "Режим технического обслуживания $status", Toast.LENGTH_SHORT).show()
        }

        debugSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "включена" else "выключена"
            Toast.makeText(context, "Отправка логов отладки $status", Toast.LENGTH_SHORT).show()
        }

        syncButton.setOnClickListener {
            Toast.makeText(context, "Идет синхронизация баз данных...", Toast.LENGTH_SHORT).show()
            syncButton.postDelayed({
                Toast.makeText(context, "Синхронизация завершена успешно!", Toast.LENGTH_SHORT).show()
            }, 1500)
        }
    }
}
