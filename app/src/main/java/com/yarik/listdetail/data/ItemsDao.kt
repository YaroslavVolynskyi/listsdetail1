package com.yarik.listdetail.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDao {

    @Query("SELECT * FROM items ORDER BY id")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getById(id: Long): Flow<ItemEntity>

    @Upsert
    suspend fun upsert(item: ItemEntity): Long

    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long): Int

    @Query("UPDATE items SET text = :text WHERE id = :id")
    suspend fun updateText(text: String, id: Long): Int

    @Query("UPDATE items SET description = :description WHERE id = :id")
    suspend fun updateDescription(description: String, id: Long): Int

    @Query("UPDATE items SET isChecked = :isChecked WHERE id = :id")
    suspend fun onCheckedChanged(isChecked: Boolean, id: Long): Int

    @Query("DELETE FROM items")
    suspend fun deleteAll(): Int
}