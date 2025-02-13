package com.osamaalek.kiosklauncher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.osamaalek.kiosklauncher.R
import com.osamaalek.kiosklauncher.model.AppInfo

class SettingsAppsAdapter(
    private val apps: List<AppInfo>,
    private val context: Context
) : RecyclerView.Adapter<SettingsAppsAdapter.ViewHolder>() {

    private val selectedApps = mutableSetOf<String>()
    private val sharedPreferences = context.getSharedPreferences("kiosk_prefs", Context.MODE_PRIVATE)

    init {
        // Carregar apps já selecionados
        selectedApps.addAll(sharedPreferences.getStringSet("selected_apps", setOf()) ?: setOf())
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val name: TextView = view.findViewById(R.id.app_name)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox_app)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_app_settings, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.label
        holder.checkbox.isChecked = selectedApps.contains(app.packageName.toString())

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedApps.add(app.packageName.toString())
            } else {
                selectedApps.remove(app.packageName.toString())
            }
            sharedPreferences.edit().putStringSet("selected_apps", selectedApps).apply()
        }
    }

    override fun getItemCount(): Int = apps.size
}