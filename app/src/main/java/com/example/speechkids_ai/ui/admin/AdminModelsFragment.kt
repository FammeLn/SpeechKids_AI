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

class AdminModelsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_models, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val voskSwitch = view.findViewById<SwitchCompat>(R.id.adminEnableVoskSwitch)
        val whisperSwitch = view.findViewById<SwitchCompat>(R.id.adminEnableWhisperSwitch)
        val cacheSwitch = view.findViewById<SwitchCompat>(R.id.adminOfflineCachingSwitch)
        val clearCacheButton = view.findViewById<Button>(R.id.adminClearModelCacheButton)

        voskSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "активирована" else "деактивирована"
            Toast.makeText(context, "Локальная Vosk модель $status", Toast.LENGTH_SHORT).show()
        }

        whisperSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "активировано" else "деактивировано"
            Toast.makeText(context, "Облачное ASR $status", Toast.LENGTH_SHORT).show()
        }

        cacheSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "включено" else "выключено"
            Toast.makeText(context, "Офлайн кеширование записей $status", Toast.LENGTH_SHORT).show()
        }

        clearCacheButton.setOnClickListener {
            Toast.makeText(context, "Кеш Vosk успешно очищен!", Toast.LENGTH_LONG).show()
        }
    }
}
