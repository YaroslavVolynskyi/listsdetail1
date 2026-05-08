package com.yarik.listdetail.data

import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllItems(): Flow<List<ItemEntity>>

    fun getById(id: Long): Flow<ItemEntity>

    suspend fun upsert(item: ItemEntity): Long

    suspend fun deleteItem(itemId: Long): Int

    suspend fun updateText(text: String, id: Long): Int

    suspend fun updateDescription(description: String, id: Long): Int

    suspend fun onCheckedChanged(isChecked: Boolean, id: Long): Int
}