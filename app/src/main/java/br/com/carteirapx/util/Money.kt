package br.com.carteirapx.util

import java.text.NumberFormat
import java.util.Locale

private val brFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

fun Long.centavosToDisplay(): String = brFormat.format(this / 100.0)

/** Converte um texto digitado (ex: "1.500,00" ou "150,00" ou "150") em centavos. Retorna 0 se inválido. */
fun parseToCentavos(text: String): Long {
    val cleaned = text.trim().replace(".", "").replace(",", ".")
    val value = cleaned.toDoubleOrNull() ?: 0.0
    return Math.round(value * 100)
}

fun Long.centavosToInputText(): String = String.format(Locale("pt", "BR"), "%.2f", this / 100.0)
