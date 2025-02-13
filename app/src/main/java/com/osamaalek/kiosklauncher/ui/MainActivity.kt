package com.osamaalek.kiosklauncher.ui

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View // Add this import
import androidx.appcompat.app.AlertDialog // Add this import
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText // Add this import
import com.osamaalek.kiosklauncher.R
import com.osamaalek.kiosklauncher.util.KioskUtil

// MainActivity.kt
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar barras do sistema
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_main)

        checkPin()
        KioskUtil.startKioskMode(this)
    }

    private fun checkPin() {
        val prefs = getSharedPreferences("kiosk_prefs", MODE_PRIVATE)
        val savedPin = prefs.getString("kiosk_pin", null)

        if (savedPin != null) {
            showPinDialog(savedPin)
        }
    }

    private fun showPinDialog(savedPin: String) {
        val dialog = AlertDialog.Builder(this)
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.filters = arrayOf(InputFilter.LengthFilter(8))

        dialog.setTitle("Digite o PIN")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                if (input.text.toString() != savedPin) {
                    finish()
                }
            }
            .show()
    }
}