package com.caglaakgul.martilocationtrackercase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.caglaakgul.martilocationtrackercase.presentation.tracking.TrackingScreen
import com.caglaakgul.martilocationtrackercase.ui.theme.MartiLocationTrackerCaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MartiLocationTrackerCaseTheme {
                TrackingScreen()
            }
        }
    }
}
