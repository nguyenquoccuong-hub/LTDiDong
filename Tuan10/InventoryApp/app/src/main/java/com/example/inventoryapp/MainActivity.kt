package com.example.inventoryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.rememberNavController
import com.example.inventoryapp.ui.navigation.InventoryNavHost
import com.example.inventoryapp.ui.theme.InventoryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventoryAppTheme {
                val navController = rememberNavController()
                InventoryNavHost(
                    navController = navController,
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()
                )
            }
        }
    }
}
