package com.yarik.listdetail.data

import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllItems(): Flow<List<ItemEntity>>

    fun getById(id: Long): Flow<ItemEntity>

    suspend fun addItem(item: ItemEntity): Long

    suspend fun deleteItem(itemId: Long): Int
}