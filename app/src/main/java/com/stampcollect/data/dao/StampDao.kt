package com.stampcollect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.stampcollect.data.entity.StampEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StampDao {
    @Query("SELECT * FROM stamps WHERE collectionId = :collectionId ORDER BY timestamp DESC")
    fun getStampsForCollection(collectionId: Int): Flow<List<StampEntity>>

    @Query("SELECT * FROM stamps ORDER BY timestamp DESC")
    fun getAllStamps(): Flow<List<StampEntity>>

    @Insert
    suspend fun insertStamp(stamp: StampEntity)

    @Update
    suspend fun updateStamp(stamp: StampEntity)

    @Query("SELECT * FROM stamps WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchStamps(query: String): Flow<List<StampEntity>>

    @Query("SELECT * FROM stamps WHERE category = :category ORDER BY timestamp DESC")
    fun getStampsByCategory(category: String): Flow<List<StampEntity>>

    @Query("SELECT * FROM stamps WHERE id = :id")
    fun getStampById(id: Int): Flow<StampEntity?>

    @androidx.room.Delete
    suspend fun deleteStamp(stamp: StampEntity)

    @Query("UPDATE stamps SET category = '' WHERE category = :categoryName")
    suspend fun clearStampsCategory(categoryName: String)

    @Query("UPDATE stamps SET category = :newName WHERE category = :oldName")
    suspend fun updateStampsCategoryName(oldName: String, newName: String)

    // Analytics queries
    @Query("SELECT * FROM stamps WHERE latitude IS NOT NULL AND longitude IS NOT NULL")
    fun getStampsWithLocation(): Flow<List<StampEntity>>

    @Query("SELECT category, COUNT(*) as count FROM stamps GROUP BY category")
    fun getStampCountByCategory(): Flow<List<CategoryCount>>

    @Query("SELECT country, COUNT(*) as count FROM stamps WHERE country != '' GROUP BY country")
    fun getStampCountByCountry(): Flow<List<CountryCount>>

    @Query("SELECT COUNT(*) FROM stamps")
    fun getTotalStampCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM stamps WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>
}

data class CategoryCount(val category: String, val count: Int)
data class CountryCount(val country: String, val count: Int)
