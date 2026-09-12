package br.com.carteirapx.ui.navigation

sealed class Routes(val route: String, val label: String) {
    object Dashboard : Routes("dashboard", "Dashboard")
    object Extrato : Routes("extrato", "Extrato")
    object Resumo : Routes("resumo", "Resumo")
    object Contas : Routes("contas", "Contas")
    object Categorias : Routes("categorias", "Categorias")
    object Detalhe : Routes("detalhe/{transactionId}", "Detalhe") {
        fun build(id: Long) = "detalhe/$id"
    }

    companion object {
        /** Itens que aparecem na bottom nav (a posição do meio é reservada para o FAB "Lançar"). */
        val bottomBarItems = listOf(Dashboard, Extrato, Resumo, Categorias)
    }
}
