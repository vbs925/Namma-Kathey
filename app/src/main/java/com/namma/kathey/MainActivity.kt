package com.namma.kathey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.namma.kathey.ui.Screen
import com.namma.kathey.ui.badge.BadgeScreen
import com.namma.kathey.ui.home.HomeScreen
import com.namma.kathey.ui.map.AiGuideScreen
import com.namma.kathey.ui.quiz.QuizScreen
import com.namma.kathey.ui.settings.SettingsScreen
import com.namma.kathey.ui.settings.SettingsViewModel
import com.namma.kathey.ui.story.StoryScreen
import com.namma.kathey.ui.theme.NammaKatheyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val darkMode by settingsVm.darkMode.collectAsState()

            NammaKatheyTheme(darkTheme = darkMode) {
                val nav = rememberNavController()

                NavHost(navController = nav, startDestination = Screen.Home.route) {

                    composable(Screen.Home.route) {
                        HomeScreen(onNavigate = { nav.navigate(it) })
                    }

                    composable(
                        Screen.Story.route,
                        arguments = listOf(navArgument("heroId") { type = NavType.IntType })
                    ) {
                        StoryScreen(
                            onNavigate = { nav.navigate(it) },
                            onBack    = { nav.popBackStack() }
                        )
                    }

                    composable(
                        Screen.Quiz.route,
                        arguments = listOf(navArgument("heroId") { type = NavType.IntType })
                    ) {
                        QuizScreen(
                            onBack     = { nav.popBackStack() },
                            onNavigate = { route ->
                                nav.navigate(route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }

                    composable(Screen.Badge.route) {
                        BadgeScreen(onBack = { nav.popBackStack() })
                    }

                    composable(Screen.AiGuide.route) {
                        AiGuideScreen(onBack = { nav.popBackStack() })
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
