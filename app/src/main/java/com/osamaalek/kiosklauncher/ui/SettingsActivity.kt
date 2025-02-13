package com.osamaalek.kiosklauncher.ui

import android.content.SharedPreferences // Add this import
import android.os.Bundle // Add this import
import android.text.InputFilter
import android.text.InputType
import android.widget.Button // Add this import
import android.widget.EditText // Add this import
import androidx.appcompat.app.AppCompatActivity // Add this import
import androidx.recyclerview.widget.GridLayoutManager // Add this import
import androidx.recyclerview.widget.RecyclerView // Add this import
import com.osamaalek.kiosklauncher.R
import com.osamaalek.kiosklauncher.util.AppsUtil // Ensure this exists
import com.osamaalek.kiosklauncher.adapter.SettingsAppsAdapter // Ensure this exists

class SettingsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pinEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("kiosk_prefs", MODE_PRIVATE)

        setupViews()
        loadApps()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView_settings)
        pinEditText = findViewById(R.id.editText_pin)
        saveButton = findViewById(R.id.button_save)

        pinEditText.filters = arrayOf(InputFilter.LengthFilter(8))
        pinEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadApps() {
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        val apps = AppsUtil.getAllApps(this)
        recyclerView.adapter = SettingsAppsAdapter(apps, this)
    }

    private fun saveSettings() {
        val pin = pinEditText.text.toString()
        if (pin.isNotEmpty()) {
            sharedPreferences.edit().putString("kiosk_pin", pin).apply()
        }
        finish()
    }
}