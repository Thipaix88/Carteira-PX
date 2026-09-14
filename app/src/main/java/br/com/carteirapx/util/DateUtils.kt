package br.com.carteirapx.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** O DatePicker do Compose trabalha em UTC — converte para/de meio-dia local, evitando o bug do "dia anterior" em fusos negativos como o do Brasil. */
fun localDateToUtcMillis(localMillis: Long): Long {
    val local = Calendar.getInstance()
    local.timeInMillis = localMillis
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.clear()
    utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    return utc.timeInMillis
}

fun utcMillisToLocalDate(utcMillis: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.timeInMillis = utcMillis
    val local = Calendar.getInstance()
    local.clear()
    local.set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH), 12, 0, 0)
    return local.timeInMillis
}

/** Intervalo do mês, deslocado por [offsetMeses] a partir do mês atual (negativo = meses passados, positivo = futuros). */
fun monthRange(offsetMeses: Int): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, offsetMeses)
    cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.MONTH, 1); cal.add(Calendar.MILLISECOND, -1)
    return start to cal.timeInMillis
}

/** Rótulo curto do mês (ex: "Set"), deslocado a partir do mês atual. */
fun monthShortLabel(offsetMeses: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, offsetMeses)
    return SimpleDateFormat("MMM", Locale("pt", "BR")).format(cal.time).replaceFirstChar { it.uppercase() }
}

/** Rótulo completo do mês (ex: "Setembro 2026"), deslocado a partir do mês atual. */
fun monthFullLabel(offsetMeses: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, offsetMeses)
    return SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR")).format(cal.time).replaceFirstChar { it.uppercase() }
}
