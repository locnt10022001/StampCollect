package com.stampcollect.data.repository

import com.stampcollect.data.dao.CollectionDao
import com.stampcollect.data.dao.StampDao
import com.stampcollect.data.dao.CategoryDao
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.data.entity.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StampRepository @Inject constructor(
    private val collectionDao: CollectionDao,
    private val stampDao: StampDao,
    private val categoryDao: CategoryDao
) {
    fun getAllCollections(): Flow<List<CollectionEntity>> = collectionDao.getAllCollections().flowOn(Dispatchers.IO)

    suspend fun insertCollection(collection: CollectionEntity) {
        collectionDao.insertCollection(collection)
    }

    suspend fun updateCollection(collection: CollectionEntity) {
        collectionDao.updateCollection(collection)
    }

    fun getStampsForCollection(collectionId: Int): Flow<List<StampEntity>> = stampDao.getStampsForCollection(collectionId).flowOn(Dispatchers.IO)

    fun getAllStamps(): Flow<List<StampEntity>> = stampDao.getAllStamps().flowOn(Dispatchers.IO)

    suspend fun insertStamp(stamp: StampEntity) {
        stampDao.insertStamp(stamp)
    }

    suspend fun updateStamp(stamp: StampEntity) {
        stampDao.updateStamp(stamp)
    }

    fun searchStamps(query: String): Flow<List<StampEntity>> = stampDao.searchStamps(query).flowOn(Dispatchers.IO)

    fun getStampsByCategory(category: String): Flow<List<StampEntity>> = stampDao.getStampsByCategory(category).flowOn(Dispatchers.IO)

    fun getStampById(id: Int): Flow<StampEntity?> = stampDao.getStampById(id).flowOn(Dispatchers.IO)

    suspend fun deleteStamp(stamp: StampEntity) {
        stampDao.deleteStamp(stamp)
    }

    suspend fun deleteCollection(collection: CollectionEntity) {
        collectionDao.deleteCollection(collection)
    }

    // Category
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories().flowOn(Dispatchers.IO)
    suspend fun insertCategory(category: CategoryEntity) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)
    suspend fun clearStampsCategory(categoryName: String) = stampDao.clearStampsCategory(categoryName)
    suspend fun updateStampsCategoryName(oldName: String, newName: String) = stampDao.updateStampsCategoryName(oldName, newName)
}
