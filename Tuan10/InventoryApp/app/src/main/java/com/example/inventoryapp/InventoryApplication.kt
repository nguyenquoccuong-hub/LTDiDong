package com.example.inventoryapp

import android.app.Application
import com.example.inventoryapp.data.InventoryDatabase
import com.example.inventoryapp.data.ItemsRepository
import com.example.inventoryapp.data.OfflineItemsRepository

class InventoryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: android.content.Context) : AppContainer {
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }
}
