package com.osamaalek.kiosklauncher.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.osamaalek.kiosklauncher.R
import com.osamaalek.kiosklauncher.adapter.AppsAdapter
import com.osamaalek.kiosklauncher.util.AppsUtil
import com.osamaalek.kiosklauncher.util.BlockAppsService

class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!hasUsageStatsPermission(this)) {
            AlertDialog.Builder(this)
                .setTitle("Permissão Necessária")
                .setMessage("Para o funcionamento correto do app, é necessário conceder acesso aos dados de uso.")
                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(intent)
                }
                .setCancelable(false)
                .show()
        }

        sharedPreferences = getSharedPreferences("kiosk_prefs", MODE_PRIVATE)
        recyclerView = findViewById(R.id.appListRecyclerView)

        settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                setupRecyclerView()
            }
        }

        setupRecyclerView()
        setupClickListener()

        val serviceIntent = Intent(this, BlockAppsService::class.java)
        startService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        val serviceIntent = Intent(this, BlockAppsService::class.java)
        startService(serviceIntent)
    }

    private fun setupRecyclerView() {
        recyclerView!!.layoutManager = GridLayoutManager(this, 4)

        val selectedApps = sharedPreferences?.getStringSet("selected_apps", emptySet<String>()) ?: emptySet()

        val apps = AppsUtil.getAllApps(this).filter { selectedApps.contains(it.packageName) }

        recyclerView!!.adapter = AppsAdapter(apps, this)
    }

    private fun setupClickListener() {
        findViewById<View>(R.id.mainLayout).setOnClickListener {
            clickCount++
            if (clickCount >= 5) {
                showPinDialog()
                clickCount = 0
            }
        }
    }

    private fun showPinDialog() {
        val dialog = AlertDialog.Builder(this)
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.filters = arrayOf<InputFilter>(LengthFilter(8))

        dialog.setTitle("Digite o PIN")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                val savedPin = sharedPreferences!!.getString("kiosk_pin", "")
                if (input.text.toString() == savedPin) {
                    settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
                }
            }
            .setNegativeButton("Cancelar") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onBackPressed() {
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) return super.onKeyDown(keyCode, event)

        return when (keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_APP_SWITCH -> true
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(AppOpsManager::class.java)
        val uid = context.applicationInfo.uid
        val mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid, context.packageName)

        return mode == AppOpsManager.MODE_ALLOWED
    }

}
