package com.selfreboot

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        statusText = findViewById(R.id.statusText)
        val scheduleButton: Button = findViewById(R.id.scheduleButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val permissionButton: Button = findViewById(R.id.permissionButton)

        timePicker.setIs24HourView(true)
        val (hour, minute) = AlarmScheduler.getSavedTime(this)
        timePicker.hour = hour
        timePicker.minute = minute

        scheduleButton.setOnClickListener {
            AlarmScheduler.scheduleDaily(this, timePicker.hour, timePicker.minute)
            updateStatus()
        }

        cancelButton.setOnClickListener {
            AlarmScheduler.cancel(this)
            updateStatus()
        }

        permissionButton.setOnClickListener {
            maybeOpenExactAlarmSettings()
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val enabled = AlarmScheduler.isEnabled(this)
        val exactAlarmAllowed = canScheduleExactAlarms(this)

        statusText.text = if (enabled) {
            val (h, m) = AlarmScheduler.getSavedTime(this)
            "Agendado para %02d:%02d diariamente.\nPermissão de alarme exato: %s"
                .format(h, m, if (exactAlarmAllowed) "OK" else "pendente")
        } else {
            "Sem agendamento ativo.\nPermissão de alarme exato: %s"
                .format(if (exactAlarmAllowed) "OK" else "pendente")
        }
    }

    private fun maybeOpenExactAlarmSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager.canScheduleExactAlarms()) return

        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    private fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }
}
