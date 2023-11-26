package com.alexmercerind.strider.ui

interface Destinations {
    val name: String

    companion object {
        object Permissions : Destinations {
            override val name = "Permissions"
        }
    }
}
