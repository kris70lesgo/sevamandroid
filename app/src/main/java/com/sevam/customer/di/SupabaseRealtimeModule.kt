package com.sevam.customer.di

import com.sevam.core.realtime.SevamRealtimeClient
import com.sevam.core.realtime.SupabaseRealtimeClient
import com.sevam.core.realtime.SupabaseRealtimeConfig
import com.sevam.customer.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseRealtimeModule {

    @Provides
    @Singleton
    fun provideSupabaseRealtimeConfig(): SupabaseRealtimeConfig {
        return SupabaseRealtimeConfig(
            projectUrl = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
            schema = BuildConfig.SUPABASE_SCHEMA,
            workerLocationsTable = BuildConfig.SUPABASE_WORKER_LOCATIONS_TABLE,
        )
    }

    @Provides
    @Singleton
    fun provideSevamRealtimeClient(
        config: SupabaseRealtimeConfig,
    ): SevamRealtimeClient = SupabaseRealtimeClient(config)
}
