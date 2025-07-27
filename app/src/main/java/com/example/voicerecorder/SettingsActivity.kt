package com.example.voicerecorder

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.voicerecorder.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSettings()
    }

    private fun setupSettings() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        // Ustawienia jakości
        val currentQuality = prefs.getString("quality", "medium")
        when (currentQuality) {
            "low" -> binding.radioLow.isChecked = true
            "high" -> binding.radioHigh.isChecked = true
            else -> binding.radioMedium.isChecked = true
        }

        binding.radioGroupQuality.setOnCheckedChangeListener { _, checkedId ->
            val quality = when (checkedId) {
                R.id.radioLow -> "low"
                R.id.radioHigh -> "high"
                else -> "medium"
            }
            prefs.edit().putString("quality", quality).apply()
        }

        // Tryb ciemny
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Auto-tagowanie
        val autoTagging = prefs.getBoolean("auto_tagging", true)
        binding.switchAutoTagging.isChecked = autoTagging

        binding.switchAutoTagging.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("auto_tagging", isChecked).apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}