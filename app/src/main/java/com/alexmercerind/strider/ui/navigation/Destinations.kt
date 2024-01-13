package com.alexmercerind.strider.ui.navigation

open class Destinations(val route: String) {

    companion object {
        object HomeScreen : Destinations("HomeScreen")

        object PermissionsScreen : Destinations("PermissionsScreen")

        object UserDetailsScreen : Destinations("UserDetailsScreen")

        object SettingsScreen : Destinations("SettingsScreen")

        object AnalyticsScreen : Destinations("AnalyticsScreen")
    }
}
