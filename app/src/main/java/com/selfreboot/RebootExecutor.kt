package com.selfreboot

object RebootExecutor {
    fun tryRebootWithRoot(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }
}
