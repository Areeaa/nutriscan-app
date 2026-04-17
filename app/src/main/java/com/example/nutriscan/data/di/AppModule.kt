package com.example.nutriscan.data.di

import android.content.Context // <-- INI YANG BENAR
import com.example.nutriscan.data.network.CloudinaryApi
import com.example.nutriscan.data.repository.HistoryRepositoryImpl
import com.example.nutriscan.data.repository.UserRepositoryImpl
import com.example.nutriscan.domain.repository.HistoryRepository
import com.example.nutriscan.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext // <-- TAMBAHKAN INI
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        cloudinaryApi: CloudinaryApi,
        @ApplicationContext context: Context // <-- TAMBAHKAN @ApplicationContext DI SINI
    ): UserRepository {
        return UserRepositoryImpl(firestore, auth, cloudinaryApi, context)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(firestore: FirebaseFirestore): HistoryRepository {
        return HistoryRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideCloudinaryApi(): CloudinaryApi {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}