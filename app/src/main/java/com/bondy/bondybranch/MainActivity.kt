package com.bondy.bondybranch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bondy.bondybranch.navigation.AppNavigation
import com.bondy.bondybranch.ui.theme.BondyBranchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BondyBranchTheme {
                AppNavigation()
            }
        }
    }
}
