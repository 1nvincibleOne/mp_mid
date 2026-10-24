package com.example.frontdesk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.frontdesk.ui.FrontDeskApp
import com.example.frontdesk.ui.theme.FrontDeskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontDeskTheme {
                FrontDeskApp()
            }
        }
    }
}
