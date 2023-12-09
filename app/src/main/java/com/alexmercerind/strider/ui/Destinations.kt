package com.alexmercerind.strider.ui

interface Destinations {
    val name: String

    companion object {
        object Home: Destinations {
            override val name = "Home"
        }

        object Permissions : Destinations {
            override val name = "Permissions"
        }

        object UserDetailsScreen: Destinations {
            override val name = "UserDetails"
        }
    }
}
