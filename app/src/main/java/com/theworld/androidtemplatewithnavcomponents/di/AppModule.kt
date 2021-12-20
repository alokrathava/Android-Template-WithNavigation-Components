package com.theworld.androidtemplatewithnavcomponents.di

import com.google.gson.GsonBuilder
import com.theworld.androidtemplatewithnavcomponents.network.Api
import com.theworld.androidtemplatewithnavcomponents.utils.baseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*  @Singleton
      @Provides
      fun provideSharedPref(
          @ApplicationContext context: Context
      ) = SharedPrefManager(context)*/


    /*  @Provides
      @Singleton
      @Named("token")
      fun provideToken(
          sharedPrefManager: SharedPrefManager
      ) = sharedPrefManager.getString("token")*/

    @Provides
    @Named("imageUrl")
    fun provideImageUrl(
        @Named("baseUrl") baseUrl: String
    ) = baseUrl + "storage/"


    @Provides
    @Named("baseUrl")
    fun provideBaseUrl() = baseUrl


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer ")
                    .build()
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, @Named("baseUrl") BASE_URL: String): Retrofit {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("${BASE_URL}matchapi/api/")
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(Api::class.java)

}