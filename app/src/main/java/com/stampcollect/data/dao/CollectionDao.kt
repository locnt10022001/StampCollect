package com.stampcollect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.stampcollect.data.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY orderIndex ASC, timestamp DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Insert
    suspend fun insertCollection(collection: CollectionEntity)

    @androidx.room.Update
    suspend fun updateCollection(collection: CollectionEntity)

    @androidx.room.Delete
    suspend fun deleteCollection(collection: CollectionEntity)
}
