package hu.pufffissal.countdownevents.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class IconOption(
    val key: String,
    val image: ImageVector,
)

// ~30 minimal, outlined icons
val IconCatalog: List<IconOption> = listOf(
    IconOption("event", Icons.Outlined.Event),
    IconOption("schedule", Icons.Outlined.Schedule),
    IconOption("alarm", Icons.Outlined.Alarm),
    IconOption("celebration", Icons.Outlined.Celebration),
    IconOption("cake", Icons.Outlined.Cake),
    IconOption("flight", Icons.Outlined.Flight),
    IconOption("train", Icons.Outlined.Train),
    IconOption("directions_car", Icons.Outlined.DirectionsCar),
    IconOption("work", Icons.Outlined.WorkOutline),
    IconOption("school", Icons.Outlined.School),
    IconOption("fitness", Icons.Outlined.FitnessCenter),
    IconOption("favorite", Icons.Outlined.FavoriteBorder),
    IconOption("home", Icons.Outlined.Home),
    IconOption("travel", Icons.Outlined.Public),
    IconOption("movie", Icons.Outlined.Movie),
    IconOption("music", Icons.Outlined.MusicNote),
    IconOption("sports", Icons.Outlined.SportsSoccer),
    IconOption("hiking", Icons.Outlined.Terrain),
    IconOption("restaurant", Icons.Outlined.Restaurant),
    IconOption("shopping", Icons.Outlined.ShoppingBag),
    IconOption("gift", Icons.Outlined.Redeem),
    IconOption("star", Icons.Outlined.StarOutline),
    IconOption("bolt", Icons.Outlined.Bolt),
    IconOption("pets", Icons.Outlined.Pets),
    IconOption("local_fire", Icons.Outlined.LocalFireDepartment),
    IconOption("beach", Icons.Outlined.BeachAccess),
    IconOption("book", Icons.Outlined.MenuBook),
    IconOption("phone", Icons.Outlined.PhoneIphone),
    IconOption("computer", Icons.Outlined.Computer),
    IconOption("wallet", Icons.Outlined.AccountBalanceWallet),
)

fun iconByKey(key: String?): ImageVector? = IconCatalog.firstOrNull { it.key == key }?.image

