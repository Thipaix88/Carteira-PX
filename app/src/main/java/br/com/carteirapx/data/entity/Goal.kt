package br.com.carteirapx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Stub para a Fase 2 (metas financeiras) — já no schema para não exigir migração dolorosa depois. */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Long, // centavos
    val currentAmount: Long = 0,
    val targetDate: Long? = null,
    val archived: Boolean = false
)
