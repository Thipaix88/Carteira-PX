package br.com.carteirapx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromAccountId: Long,
    val toAccountId: Long,
    val amount: Long, // centavos
    val date: Long, // epoch millis
    val notes: String = ""
)
