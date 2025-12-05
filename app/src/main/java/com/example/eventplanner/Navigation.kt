package com.example.eventplanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    object EditEvent : Screen("edit_event/{eventId}/{eventTitle}/{eventDate}/{eventDescription}") {
        fun createRoute(event: Event): String {
            return "edit_event/${event.id}/${event.title}/${event.date}/${event.description}"
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

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
                    navController.navigate(Screen.EditEvent.createRoute(event))
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

        composable(Screen.EditEvent.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val eventTitle = backStackEntry.arguments?.getString("eventTitle") ?: ""
            val eventDate = backStackEntry.arguments?.getString("eventDate") ?: ""
            val eventDescription = backStackEntry.arguments?.getString("eventDescription") ?: ""

            val event = Event(
                id = eventId,
                title = eventTitle,
                date = eventDate,
                description = eventDescription
            )

            EditEventScreen(
                event = event,
                eventViewModel = eventViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}