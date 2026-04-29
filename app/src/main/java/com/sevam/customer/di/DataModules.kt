/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sevam.customer.di

import android.content.Context
import androidx.room.Room
import com.sevam.customer.data.DefaultTaskRepository
import com.sevam.customer.data.TaskRepository
import com.sevam.customer.data.source.local.TaskDao
import com.sevam.customer.data.source.local.SevamDatabase
import com.sevam.customer.data.source.network.NetworkDataSource
import com.sevam.customer.data.source.network.TaskNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: TaskNetworkDataSource): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): SevamDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SevamDatabase::class.java,
            "SevamCustomer.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: SevamDatabase): TaskDao = database.taskDao()
}
