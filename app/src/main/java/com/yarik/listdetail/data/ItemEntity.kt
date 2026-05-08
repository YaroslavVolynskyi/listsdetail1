package com.yarik.listdetail.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String? = null,
    val description: String? = null,
    val isChecked: Boolean = false
)