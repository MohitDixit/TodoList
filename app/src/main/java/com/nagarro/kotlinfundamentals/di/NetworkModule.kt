package com.nagarro.kotlinfundamentals.di

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.nagarro.kotlinfundamentals.BuildConfig
import com.nagarro.kotlinfundamentals.api.ApiInterface
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(context:Context): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val cacheDir = File(context.cacheDir, UUID.randomUUID().toString())
        val cache = Cache(cacheDir, BuildConfig.cacheSize * BuildConfig.cacheUnit * BuildConfig.cacheUnit)
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(BuildConfig.connTimeout, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.rwTimeout, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.rwTimeout, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(gson: Gson, okHttpClient: OkHttpClient): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build().create(ApiInterface::class.java)
    }
}