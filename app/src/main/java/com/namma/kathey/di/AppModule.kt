package com.namma.kathey.di

import android.content.Context
import androidx.room.Room
import com.namma.kathey.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration().build()

    @Provides fun provideHeroDao(db: AppDatabase)  = db.heroDao()
    @Provides fun provideBadgeDao(db: AppDatabase) = db.badgeDao()

    @Provides @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}
