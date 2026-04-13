package com.example.nutriscan.data.di

import com.example.nutriscan.data.repository.HistoryRepositoryImpl
import com.example.nutriscan.data.repository.UserRepositoryImpl
import com.example.nutriscan.domain.repository.HistoryRepository
import com.example.nutriscan.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(firestore: FirebaseFirestore): HistoryRepository {
        return HistoryRepositoryImpl(firestore)
    }


}