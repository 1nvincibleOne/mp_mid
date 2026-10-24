package com.example.frontdesk.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun FrontDeskApp(viewModel: FrontDeskViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                state = state,
                onFilterChange = viewModel::setFilter,
                onGuestClick = { room -> navController.navigate("guest/$room") },
                onCheckInClick = {
                    viewModel.startCheckIn()
                    navController.navigate("checkin")
                },
                onHistoryClick = { navController.navigate("history") },
            )
        }
        composable("checkin") {
            CheckInScreen(
                state = state,
                onPlateChange = viewModel::setPlate,
                onScanPlate = viewModel::scanPlate,
                onCheckIn = viewModel::checkIn,
                onBack = { navController.navigateUp() },
            )
        }
        composable(
            route = "guest/{room}",
            arguments = listOf(navArgument("room") { type = NavType.IntType }),
        ) { entry ->
            val room = entry.arguments?.getInt("room") ?: 0
            GuestScreen(
                state = state,
                roomNumber = room,
                onCheckOut = {
                    viewModel.checkOut(room)
                    navController.navigateUp()
                },
                onBack = { navController.navigateUp() },
            )
        }
        composable("history") {
            HistoryScreen(state = state, onBack = { navController.navigateUp() })
        }
    }
}
