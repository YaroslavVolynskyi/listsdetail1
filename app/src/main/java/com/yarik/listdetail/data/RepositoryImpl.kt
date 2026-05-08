package com.yarik.listdetail.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    val itemsDao: ItemsDao
) : Repository {

    override fun getAllItems(): Flow<List<ItemEntity>> {
        return itemsDao.getAllItems()
    }

    override fun getById(id: Long): Flow<ItemEntity> {
        return itemsDao.getById(id)
    }

    override suspend fun upsert(item: ItemEntity): Long {
        return itemsDao.upsert(item)
    }

    override suspend fun deleteItem(itemId: Long): Int {
        return itemsDao.deleteItem(itemId)
    }

    override suspend fun updateText(text: String, id: Long): Int {
        return itemsDao.updateText(text, id)
    }

    override suspend fun onCheckedChanged(isChecked: Boolean, id: Long): Int {
        return itemsDao.onCheckedChanged(isChecked, id)
    }
}