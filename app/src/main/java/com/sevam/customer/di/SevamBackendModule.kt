package com.sevam.customer.di

import android.app.Application
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sevam.core.location.LocationSearchClient
import com.sevam.core.location.MockLocationSearchClient
import com.sevam.core.location.RemoteLocationSearchClient
import com.sevam.core.network.SevamApiConfig
import com.sevam.core.network.SevamApiService
import com.sevam.core.payments.RazorpayGateway
import com.sevam.core.payments.StubRazorpayGateway
import com.sevam.core.security.SessionStore
import com.sevam.customer.BuildConfig
import com.sevam.customer.sevam.data.AddressRepository
import com.sevam.customer.sevam.data.AuthRepository
import com.sevam.customer.sevam.data.BookingsRepository
import com.sevam.customer.sevam.data.CatalogRepository
import com.sevam.customer.sevam.data.DefaultAddressRepository
import com.sevam.customer.sevam.data.DefaultAuthRepository
import com.sevam.customer.sevam.data.DefaultBookingsRepository
import com.sevam.customer.sevam.data.DefaultCatalogRepository
import com.sevam.customer.sevam.data.DefaultNotificationsRepository
import com.sevam.customer.sevam.data.DefaultPaymentsRepository
import com.sevam.customer.sevam.data.DefaultProfileRepository
import com.sevam.customer.sevam.data.NotificationsRepository
import com.sevam.customer.sevam.data.PaymentsRepository
import com.sevam.customer.sevam.data.ProfileRepository
import com.sevam.customer.sevam.data.SupabaseAuthConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SevamRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: DefaultAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(repository: DefaultCatalogRepository): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(repository: DefaultProfileRepository): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(repository: DefaultAddressRepository): AddressRepository

    @Binds
    @Singleton
    abstract fun bindBookingsRepository(repository: DefaultBookingsRepository): BookingsRepository

    @Binds
    @Singleton
    abstract fun bindPaymentsRepository(repository: DefaultPaymentsRepository): PaymentsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(repository: DefaultNotificationsRepository): NotificationsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object SevamBackendModule {
    @Provides
    @Singleton
    fun provideSessionStore(application: Application): SessionStore = SessionStore.from(application)

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideApiConfig(): SevamApiConfig = SevamApiConfig(BuildConfig.SEVAM_API_BASE_URL)

    @Provides
    @Singleton
    fun provideSupabaseAuthConfig(): SupabaseAuthConfig {
        return SupabaseAuthConfig(
            projectUrl = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionStore: SessionStore,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = sessionStore.restore()?.accessToken
                val request = if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    Request.Builder()
                        .url(chain.request().url)
                        .headers(chain.request().headers)
                        .method(chain.request().method, chain.request().body)
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @OptIn(ExperimentalSerializationApi::class)
    fun provideSevamApiService(
        apiConfig: SevamApiConfig,
        okHttpClient: OkHttpClient,
        json: Json,
    ): SevamApiService {
        val baseUrl = apiConfig.normalizedBaseUrl().ifBlank { "https://example.invalid/api/" }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SevamApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationSearchClient(
        apiConfig: SevamApiConfig,
        apiService: SevamApiService,
        json: Json,
    ): LocationSearchClient {
        return if (apiConfig.isConfigured) {
            RemoteLocationSearchClient(
                apiConfig = apiConfig,
                apiService = apiService,
                json = json,
            )
        } else {
            MockLocationSearchClient()
        }
    }

    @Provides
    @Singleton
    fun provideRazorpayGateway(): RazorpayGateway = StubRazorpayGateway()
}
