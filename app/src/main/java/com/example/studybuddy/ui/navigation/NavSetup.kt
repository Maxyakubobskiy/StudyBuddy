package com.example.studybuddy.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studybuddy.ui.screen.home.HomeScreen
import com.example.studybuddy.ui.screen.homework.HomeworkItemScreen
import com.example.studybuddy.ui.screen.homework.HomeworkScreen
import com.example.studybuddy.ui.screen.schedule.ScheduleScreen
import com.example.studybuddy.ui.screen.subjects.SubjectScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavSetup() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.HomeWork.route) { HomeworkScreen(navController) }
        composable(Screen.Schedule.route) { ScheduleScreen(navController) }
        composable(Screen.Subjects.route) { SubjectScreen(navController) }
        composable(route = Screen.HomeworkItem.route+"/{homeworkId}", arguments = listOf(navArgument("homeworkId") { type = NavType.LongType })) {
            backStackEntry ->
            val homeworkId = backStackEntry.arguments?.getLong("homeworkId") ?: -1L
            HomeworkItemScreen(homeworkId = homeworkId, navController = navController)
        }
    }
}