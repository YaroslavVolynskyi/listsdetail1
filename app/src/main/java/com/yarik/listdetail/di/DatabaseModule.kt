package com.yarik.listdetail.di

import android.content.Context
import androidx.room.Room
import com.yarik.listdetail.data.ItemsDao
import com.yarik.listdetail.data.ItemsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): ItemsDatabase =
        Room.databaseBuilder(context = context, ItemsDatabase::class.java, "items.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun providesDao(database: ItemsDatabase): ItemsDao {
        return database.itemsDao()
    }
}