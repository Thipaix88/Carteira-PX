package br.com.carteirapx.ui.categorias

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

/** Ícones disponíveis para categorias. A chave é o que fica salvo no banco (Category.iconName). */
object CategoryIcons {
    val map: Map<String, ImageVector> = linkedMapOf(
        "category" to Icons.Default.Category,
        "food" to Icons.Default.Fastfood,
        "market" to Icons.Default.LocalGroceryStore,
        "car" to Icons.Default.DirectionsCar,
        "home" to Icons.Default.Home,
        "health" to Icons.Default.LocalHospital,
        "school" to Icons.Default.School,
        "shopping" to Icons.Default.ShoppingCart,
        "salary" to Icons.Default.AttachMoney,
        "gift" to Icons.Default.CardGiftcard,
        "bills" to Icons.Default.Receipt,
        "movie" to Icons.Default.Movie,
        "travel" to Icons.Default.Flight,
        "pet" to Icons.Default.Pets,
        "gym" to Icons.Default.FitnessCenter,
        "internet" to Icons.Default.Wifi,
        "games" to Icons.Default.SportsEsports
    )

    fun iconFor(name: String): ImageVector = map[name] ?: Icons.Default.Category
}
