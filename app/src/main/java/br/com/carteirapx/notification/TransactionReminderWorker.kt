package br.com.carteirapx.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class TransactionReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transactionRepo: TransactionRepository,
    private val notifier: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Primeiro atualiza os status pela data de hoje — sem isso, um lançamento previsto
        // nunca vira "atrasado" sozinho, ele fica congelado no status de quando foi criado.
        transactionRepo.refreshStatuses()

        val todos = transactionRepo.observeAll().first()
        val vencendoHoje = todos.count { it.status == TransactionStatus.VENCENDO }
        val atrasados = todos.count { it.status == TransactionStatus.ATRASADA }

        if (vencendoHoje > 0) {
            notifier.notify(
                id = 1001,
                title = "Vencendo hoje",
                text = if (vencendoHoje == 1) "Você tem 1 lançamento vencendo hoje" else "Você tem $vencendoHoje lançamentos vencendo hoje"
            )
        }
        if (atrasados > 0) {
            notifier.notify(
                id = 1002,
                title = "Lançamentos atrasados",
                text = if (atrasados == 1) "Você tem 1 lançamento atrasado" else "Você tem $atrasados lançamentos atrasados"
            )
        }
        return Result.success()
    }
}
