package com.example.eventplanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventplanner.models.Event
import com.example.eventplanner.screens.*
import com.example.eventplanner.viewmodels.AuthViewModel
import com.example.eventplanner.viewmodels.EventViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object EventList : Screen("event_list")
    object CreateEvent : Screen("create_event")
    object EditEvent : Screen("edit_event")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    val startDestination = if (isLoggedIn) Screen.EventList.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.EventList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.EventList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EventList.route) {
            EventListScreen(
                authViewModel = authViewModel,
                eventViewModel = eventViewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onNavigateToEdit = { event ->
                    selectedEvent = event
                    navController.navigate(Screen.EditEvent.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                eventViewModel = eventViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EditEvent.route) {
            selectedEvent?.let { event ->
                EditEventScreen(
                    event = event,
                    eventViewModel = eventViewModel,
                    onNavigateBack = {
                        selectedEvent = null
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}