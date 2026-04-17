package com.stampcollect.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stampcollect.data.dao.CollectionDao
import com.stampcollect.data.dao.StampDao
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.data.entity.StampEntity

import com.stampcollect.data.dao.CategoryDao
import com.stampcollect.data.entity.CategoryEntity

@Database(entities = [CollectionEntity::class, StampEntity::class, CategoryEntity::class], version = 7, exportSchema = false)
abstract class StampDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun stampDao(): StampDao
    abstract fun categoryDao(): CategoryDao
}
