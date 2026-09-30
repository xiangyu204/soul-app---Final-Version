package com.example.soul_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.soul_android.ui.theme.SOUL_AndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SOUL_AndroidTheme {
                SOULApp()
            }
        }
    }
}
