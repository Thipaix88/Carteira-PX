package br.com.carteirapx

import android.app.Application
import br.com.carteirapx.data.DefaultDataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CarteiraPxApplication : Application() {

    @Inject lateinit var seeder: DefaultDataSeeder
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { seeder.seedIfNeeded() }
    }
}
