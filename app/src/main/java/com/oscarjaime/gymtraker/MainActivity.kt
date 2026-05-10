package com.oscarjaime.gymtraker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.oscarjaime.gymtraker.ui.navigation.AppNavigation
import com.oscarjaime.gymtraker.ui.theme.GymTrackerTheme
import com.oscarjaime.gymtraker.ui.viewmodel.GymTrackerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GymTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
