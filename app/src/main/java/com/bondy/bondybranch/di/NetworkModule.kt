package com.bondy.bondybranch.di

import com.bondy.bondybranch.data.remote.api.BondyApiService
import com.bondy.bondybranch.data.remote.api.AuthRefreshApiService
import com.bondy.bondybranch.data.remote.auth.TokenAuthenticator
import com.bondy.bondybranch.data.repository.BondyRepositoryImpl
import com.bondy.bondybranch.domain.repository.BondyRepository
import com.bondy.bondybranch.utility.PreferenceStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://bondy-production.up.railway.app/"
    private const val BASE_URL_TEST = "https://postman-echo.com/"
    private const val LOCAL_HOST = "http://192.168.1.32:8000/"




    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideAuthRefreshApiService(
        gson: Gson,
        logging: HttpLoggingInterceptor
    ): AuthRefreshApiService =
        Retrofit.Builder()
            .baseUrl(LOCAL_HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
            )
            .build()
            .create(AuthRefreshApiService::class.java)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        prefs: PreferenceStorage,
        refreshApiService: AuthRefreshApiService
    ): TokenAuthenticator = TokenAuthenticator(prefs, refreshApiService)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .authenticator(authenticator)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(LOCAL_HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideBondyApiService(retrofit: Retrofit): BondyApiService =
        retrofit.create(BondyApiService::class.java)

    @Provides
    @Singleton
    fun provideBondyRepository(
        repositoryImpl: BondyRepositoryImpl
    ): BondyRepository = repositoryImpl

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IoDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultDispatcher

    @Module
    @InstallIn(SingletonComponent::class)
    object DispatcherModule {

        @IoDispatcher
        @Provides
        @Singleton
        fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @MainDispatcher
        @Provides
        @Singleton
        fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        @DefaultDispatcher
        @Provides
        @Singleton
        fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    }
}
