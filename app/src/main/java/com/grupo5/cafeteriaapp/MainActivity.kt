package com.grupo5.cafeteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.grupo5.cafeteriaapp.navigation.AppNavigation
import com.grupo5.cafeteriaapp.ui.theme.CafeteriaAppTheme
import com.grupo5.cafeteriaapp.viewmodel.AuthViewModel
import com.grupo5.cafeteriaapp.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark by themeViewModel.isDarkMode.collectAsState()
            CafeteriaAppTheme(darkTheme = isDark) {
                AppNavigation(
                    authViewModel = authViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}