package com.example.testcontentprovider.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testcontentprovider.Contact
import com.example.testcontentprovider.screens.ContactDetailsScreen
import com.example.testcontentprovider.screens.ContactsScreen

private const val CONTACT = "contact"

@Composable
fun ContactsApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.ContactsList.route
    ) {
        composable(Route.ContactsList.route) {
            ContactsScreen(
                modifier = modifier,
                onContactClick = { contact ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(CONTACT, contact)
                    navController.navigate(Route.ContactDetails.route)
                }
            )
        }
        composable(route = Route.ContactDetails.route) {
            navController.previousBackStackEntry?.savedStateHandle?.get<Contact>(CONTACT)?.let { contact ->
                ContactDetailsScreen(
                    modifier = modifier,
                    contact = contact,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}