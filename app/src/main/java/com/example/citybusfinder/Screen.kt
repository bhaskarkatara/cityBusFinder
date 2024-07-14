package com.example.citybusfinder

 sealed class Screen(val route: String) {
      data object Welcome : Screen("welcome")
      data object PermissionScreen : Screen("permission_screen")
       data object Finder:Screen("finder")
       data object HistoryScreen:Screen("history")
     data object FeedbackScreen :Screen("feedback-screen")
 }