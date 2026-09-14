package br.com.carteirapx.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * O WorkManager já sobrevive a reboots por conta própria, mas isso deixa explícito e garante
 * reagendamento mesmo em fabricantes com gerenciamento agressivo de bateria/boot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationScheduler.schedule(context)
        }
    }
}
