package br.com.carteirapx.data.entity

/** Tipo do lançamento. */
enum class TransactionType { RECEITA, DESPESA }

/**
 * Status calculado automaticamente com base nas datas — não é editável manualmente
 * pelo usuário, exceto a transição explícita para CANCELADA.
 *
 * PREVISTA   -> ainda não confirmada, data prevista no futuro
 * VENCENDO   -> ainda não confirmada, data prevista é hoje
 * ATRASADA   -> ainda não confirmada, data prevista já passou
 * CONFIRMADA -> usuário confirmou o pagamento/recebimento (dataRealizada preenchida)
 * CANCELADA  -> lançamento cancelado manualmente, não entra em nenhum cálculo de saldo
 */
enum class TransactionStatus { PREVISTA, VENCENDO, ATRASADA, CONFIRMADA, CANCELADA }

enum class RecurrenceFrequency { SEMANAL, QUINZENAL, MENSAL, BIMESTRAL, TRIMESTRAL, SEMESTRAL, ANUAL }

enum class AccountType { CORRENTE, POUPANCA, CARTEIRA, INVESTIMENTO, OUTRA }
