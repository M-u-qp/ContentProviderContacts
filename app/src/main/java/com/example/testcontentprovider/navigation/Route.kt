package com.example.testcontentprovider.navigation

sealed class Route(val route: String) {
    object ContactsList : Route("contactsScreen")
    object ContactDetails : Route("contactDetails")
}