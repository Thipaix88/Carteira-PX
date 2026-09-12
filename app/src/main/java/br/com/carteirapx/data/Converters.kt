package br.com.carteirapx.data

import androidx.room.TypeConverter
import br.com.carteirapx.data.entity.AccountType
import br.com.carteirapx.data.entity.RecurrenceFrequency
import br.com.carteirapx.data.entity.TransactionStatus
import br.com.carteirapx.data.entity.TransactionType

/** Enums são gravados como texto (o nome da constante), para ficar legível no banco e estável entre versões. */
class Converters {
    @TypeConverter fun fromTransactionType(v: TransactionType) = v.name
    @TypeConverter fun toTransactionType(v: String) = TransactionType.valueOf(v)

    @TypeConverter fun fromTransactionStatus(v: TransactionStatus) = v.name
    @TypeConverter fun toTransactionStatus(v: String) = TransactionStatus.valueOf(v)

    @TypeConverter fun fromRecurrenceFrequency(v: RecurrenceFrequency) = v.name
    @TypeConverter fun toRecurrenceFrequency(v: String) = RecurrenceFrequency.valueOf(v)

    @TypeConverter fun fromAccountType(v: AccountType) = v.name
    @TypeConverter fun toAccountType(v: String) = AccountType.valueOf(v)
}
