package br.com.carteirapx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val iconName: String = "category",
    val colorHex: String = "#607D8B",
    val archived: Boolean = false
)
