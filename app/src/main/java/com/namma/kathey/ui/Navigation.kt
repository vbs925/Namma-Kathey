package com.namma.kathey.ui

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object HeroList : Screen("hero_list/{district}") {
        fun createRoute(d: String) = "hero_list/$d"
    }
    object Story    : Screen("story/{heroId}") {
        fun createRoute(id: Int) = "story/$id"
    }
    object Quiz     : Screen("quiz/{heroId}") {
        fun createRoute(id: Int) = "quiz/$id"
    }
    object Badge    : Screen("badge")
    object AiGuide  : Screen("ai_guide")
    object Settings : Screen("settings")
}
