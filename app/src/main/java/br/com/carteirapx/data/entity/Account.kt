package br.com.carteirapx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: AccountType = AccountType.CORRENTE,
    val initialBalance: Long = 0, // guardado em centavos, para evitar erro de ponto flutuante
    val colorHex: String = "#4CAF50",
    val archived: Boolean = false
)
