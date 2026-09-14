package br.com.carteirapx.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME = "transaction_reminder_daily"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<TransactionReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
