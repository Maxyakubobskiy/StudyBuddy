package com.example.studybuddy

import android.app.StatusBarManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.studybuddy.ui.navigation.NavSetup
import com.example.studybuddy.ui.screen.splash.SplashViewModel
import com.example.studybuddy.ui.theme.StudyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = SplashViewModel()
        installSplashScreen().setKeepOnScreenCondition { viewModel.isLoading.value }
//        enableEdgeToEdge(
//            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT,Color.TRANSPARENT)
//        )
        setContent {
            StudyBuddyTheme{
            NavSetup()
            }
        }
    }
}