//package com.example.citybusfinder
//
//
////import kotlin.coroutines.jvm.internal.CompletedContinuation.context
//import androidx.compose.material.BottomNavigation
//import androidx.compose.material.BottomNavigationItem
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavController
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//
//@Composable
//fun BottomNavigationBar(navController: NavController) {
//    val items = listOf(
//        BottomNavigationScreens.Finder,
//        BottomNavigationScreens.History
//    )
//    BottomNavigation {
//        val navBackStackEntry = navController.currentBackStackEntryAsState().value
//        val currentRoute = navBackStackEntry?.destination?.route
//        items.forEach { item ->
//            BottomNavigationItem(
//                icon = { Icons.Default.Search },
//                label = { Text(text = item.label) },
//                selected = currentRoute == item.route,
//                onClick = {
//                    navController.navigate(item.route) {
//                        popUpTo(navController.graph.startDestinationId) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            )
//        }
//    }
//}
//
//
//@Composable
//
//fun MainScreen(navController: NavHostController, locationUtils: LocationUtils) {
//    Scaffold(
//        bottomBar = { BottomNavigationBar(navController) }, content = { it:paddingValues ->
//            NavHost(
//                navController = navController,
//                startDestination = BottomNavigationScreens.Finder.route
//            ) {
//                composable(BottomNavigationScreens.Finder.route) {
//                    FinderScreen(navController, viewModel = InputsViewModel(), locationUtils, LocalContext.current)
//                }
//                composable(BottomNavigationScreens.History.route) {
//                    HistoryScreen()
//
//                }
//            }
//
//        }
//    )
//}
//
//
//
//@Composable
//fun HistoryScreen() {
//    Text(text = "hii history")
//}