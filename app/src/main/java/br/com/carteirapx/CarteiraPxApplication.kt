package br.com.carteirapx

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import br.com.carteirapx.data.DefaultDataSeeder
import br.com.carteirapx.domain.usecase.GenerateRecurringOccurrencesUseCase
import br.com.carteirapx.notification.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CarteiraPxApplication : Application(), Configuration.Provider {

    @Inject lateinit var seeder: DefaultDataSeeder
    @Inject lateinit var generateRecurring: GenerateRecurringOccurrencesUseCase
    @Inject lateinit var workerFactory: HiltWorkerFactory
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        NotificationScheduler.schedule(this)
        appScope.launch {
            seeder.seedIfNeeded()
            generateRecurring()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
