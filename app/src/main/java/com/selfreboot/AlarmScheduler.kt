package com.selfreboot

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object AlarmScheduler {
    private const val REQUEST_CODE = 77
    private const val PREFS = "self_reboot_prefs"
    private const val KEY_HOUR = "scheduled_hour"
    private const val KEY_MINUTE = "scheduled_minute"
    private const val KEY_ENABLED = "scheduled_enabled"

    fun scheduleDaily(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntent(context)

        val nextTrigger = computeNextTriggerMillis(hour, minute)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            saveSchedule(context, hour, minute, enabled = true)
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTrigger,
            pendingIntent
        )

        saveSchedule(context, hour, minute, enabled = true)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, false)
            .apply()
    }

    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun getSavedTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_HOUR, 3) to prefs.getInt(KEY_MINUTE, 0)
    }

    fun rescheduleIfNeeded(context: Context) {
        if (!isEnabled(context)) return
        val (hour, minute) = getSavedTime(context)
        scheduleDaily(context, hour, minute)
    }

    private fun saveSchedule(context: Context, hour: Int, minute: Int, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .putBoolean(KEY_ENABLED, enabled)
            .apply()
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyRebootReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun computeNextTriggerMillis(hour: Int, minute: Int): Long {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)
        var scheduled = now.with(LocalTime.of(hour, minute)).withSecond(0).withNano(0)
        if (!scheduled.isAfter(now)) {
            scheduled = scheduled.plusDays(1)
        }
        return scheduled.toInstant().toEpochMilli()
    }
}
