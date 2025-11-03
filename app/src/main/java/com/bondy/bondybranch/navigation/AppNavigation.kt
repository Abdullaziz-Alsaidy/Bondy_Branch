package com.bondy.bondybranch.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bondy.bondybranch.presentation.screens.CardDetailsScreen
import com.bondy.bondybranch.presentation.screens.DashboardScreen
import com.bondy.bondybranch.presentation.screens.HistoryScreen
import com.bondy.bondybranch.presentation.screens.LoginScreen
import com.bondy.bondybranch.presentation.screens.ScanScreen
import com.bondy.bondybranch.presentation.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val bottomNavItems = listOf(
        NavItem(
            destination = Screens.Dashboard,
            label = "Dashboard",
            icon = Icons.Filled.Home
        ),
        NavItem(
            destination = Screens.Scan,
            label = "Scan",
            icon = Icons.Filled.QrCodeScanner
        ),
        NavItem(
            destination = Screens.History,
            label = "History",
            icon = Icons.Filled.History
        ),
        NavItem(
            destination = Screens.Settings,
            label = "Settings",
            icon = Icons.Filled.Settings
        )
    )

    Scaffold(
        bottomBar = {
            val shouldShowBottomBar = currentRoute != Screens.Login::class.qualifiedName
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val route = item.destination::class.qualifiedName
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(item.destination) {
                                    popUpTo<Screens.Dashboard> {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Login,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screens.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screens.Dashboard) {
                            popUpTo<Screens.Login> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<Screens.Dashboard> {
                DashboardScreen(
                    onScanClick = { navController.navigate(Screens.Scan) },
                    onHistoryClick = { navController.navigate(Screens.History) },
                    onCardSelected = { cardNumber ->
                        navController.navigate(Screens.CardDetails(cardNumber))
                    }
                )
            }
            composable<Screens.Scan> {
                ScanScreen(
                    onBack = { navController.popBackStack() },
                    onCardDetected = { cardNumber ->
                        navController.navigate(Screens.CardDetails(cardNumber))
                    }
                )
            }
            composable<Screens.CardDetails> { entry ->
                val args = entry.toRoute<Screens.CardDetails>()
                CardDetailsScreen(
                    cardNumber = args.cardNumber,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Screens.History> {
                HistoryScreen()
            }
            composable<Screens.Settings> {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Screens.Login) {
                            popUpTo<Screens.Dashboard> {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

private data class NavItem(
    val destination: Any,
    val label: String,
    val icon: ImageVector
)
