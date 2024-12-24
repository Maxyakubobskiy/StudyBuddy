package com.example.studybuddy.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studybuddy.ui.navigation.Screen

@Composable
fun NavigationBarComponent(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar (
        containerColor = MaterialTheme.colorScheme.secondary
    ){
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Homework") },
            label = { Text("Homework") },
            selected = currentRoute == Screen.HomeWork.route,
            onClick = {
                if (currentRoute != Screen.HomeWork.route) {
                    navController.navigate(Screen.HomeWork.route) {
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Schedule") },
            label = { Text("Schedule") },
            selected = currentRoute == Screen.Schedule.route,
            onClick = {
                if (currentRoute != Screen.Schedule.route) {
                    navController.navigate(Screen.Schedule.route) {
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Subjects") },
            label = { Text("Subjects") },
            selected = currentRoute == Screen.Subjects.route,
            onClick = {
                if (currentRoute != Screen.Subjects.route) {
                    navController.navigate(Screen.Subjects.route) {
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            )
        )
    }
}
