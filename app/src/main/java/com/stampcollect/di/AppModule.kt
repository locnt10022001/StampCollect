package com.stampcollect.di

import android.app.Application
import androidx.room.Room
import com.stampcollect.data.StampDatabase
import com.stampcollect.data.dao.CollectionDao
import com.stampcollect.data.dao.StampDao
import com.stampcollect.data.dao.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStampDatabase(app: Application): StampDatabase {
        return Room.databaseBuilder(
            app,
            StampDatabase::class.java,
            "stamp_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCollectionDao(db: StampDatabase): CollectionDao {
        return db.collectionDao()
    }

    @Provides
    @Singleton
    fun provideStampDao(db: StampDatabase): StampDao {
        return db.stampDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: StampDatabase): CategoryDao {
        return db.categoryDao()
    }
}
