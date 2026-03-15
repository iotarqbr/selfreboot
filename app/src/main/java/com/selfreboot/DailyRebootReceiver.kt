package com.selfreboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class DailyRebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val rebooted = RebootExecutor.tryRebootWithRoot()
        val message = if (rebooted) {
            "Comando de reboot enviado."
        } else {
            "Falha ao reiniciar: este app só funciona em dispositivos com root."
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        AlarmScheduler.rescheduleIfNeeded(context)
    }
}
