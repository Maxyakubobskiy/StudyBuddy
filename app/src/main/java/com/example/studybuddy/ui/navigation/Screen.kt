package com.example.studybuddy.ui.navigation

sealed class Screen(val route: String) {
     data object Home: Screen(route = "home")
     data object HomeWork: Screen(route = "homework")
     data object HomeworkItem: Screen(route = "homeworkItem")
     data object Schedule: Screen(route = "schedule")
     data object Subjects: Screen(route = "subjects")
}