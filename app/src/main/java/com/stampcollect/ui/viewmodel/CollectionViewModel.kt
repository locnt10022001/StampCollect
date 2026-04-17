package com.stampcollect.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.stampcollect.data.entity.CategoryEntity
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.data.repository.Resource
import com.stampcollect.data.repository.StampRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: StampRepository
) : BaseViewModel() {

    val collections: StateFlow<Resource<List<CollectionEntity>>> = wrapFlowInResource(repository.getAllCollections())
        .stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    val allStamps: StateFlow<Resource<List<StampEntity>>> = wrapFlowInResource(repository.getAllStamps())
        .stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    val categories: StateFlow<Resource<List<CategoryEntity>>> = wrapFlowInResource(repository.getAllCategories())
        .stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    private val _currentCollectionStamps = MutableStateFlow<Resource<List<StampEntity>>>(Resource.Loading())
    val currentCollectionStamps: StateFlow<Resource<List<StampEntity>>> = _currentCollectionStamps

    private val _selectedCollection = MutableStateFlow<CollectionEntity?>(null)
    val selectedCollection: StateFlow<CollectionEntity?> = _selectedCollection

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _isFavoriteFilter = MutableStateFlow(false)
    val isFavoriteFilter: StateFlow<Boolean> = _isFavoriteFilter

    val filteredStamps: StateFlow<Resource<List<StampEntity>>> = combine(
        allStamps,
        _searchQuery,
        _selectedCategory,
        _isFavoriteFilter
    ) { stampsRes, query, category, isFavoriteOnly ->
        if (stampsRes is Resource.Success) {
            val stamps = stampsRes.data
            val filtered = stamps.filter { stamp ->
                (query.isEmpty() || stamp.name.contains(query, ignoreCase = true) || stamp.description.contains(query, ignoreCase = true)) &&
                (category == null || stamp.category == category) &&
                (!isFavoriteOnly || stamp.isFavorite)
            }
            Resource.Success(filtered)
        } else {
            stampsRes as Resource<List<StampEntity>>
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    init {
        viewModelScope.launch {
            repository.getAllCollections().collect { list ->
                if (list.isEmpty()) {
                    repository.insertCollection(
                        CollectionEntity(name = "All", description = "All your stamps", orderIndex = 0)
                    )
                } else {
                    if (_selectedCollection.value == null) {
                        _selectedCollection.value = list.firstOrNull { it.name == "All" } ?: list.first()
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.getAllCategories().take(1).collect { list ->
                if (list.isEmpty()) {
                    listOf("Flora", "Fauna", "Places", "Art", "Rare").forEach {
                        repository.insertCategory(CategoryEntity(name = it))
                    }
                }
            }
        }
    }

    fun selectCollection(collection: CollectionEntity) {
        _selectedCollection.value = collection
    }

    fun addCollection(name: String, description: String) {
        viewModelScope.launch {
            repository.insertCollection(
                CollectionEntity(name = name, description = description)
            )
        }
    }

    fun updateCollectionBackground(collection: CollectionEntity, backgroundType: Int) {
        viewModelScope.launch {
            repository.updateCollection(collection.copy(backgroundType = backgroundType))
        }
    }

    fun updateCollection(collection: CollectionEntity) {
        viewModelScope.launch {
            repository.updateCollection(collection)
        }
    }

    fun loadStampsForCollection(collectionId: Int) {
        viewModelScope.launch {
            wrapFlowInResource(repository.getStampsForCollection(collectionId)).collect {
                _currentCollectionStamps.value = it
            }
        }
    }

    fun addStamp(collectionId: Int, imagePath: String, name: String, lat: Double? = null, lng: Double? = null) {
        viewModelScope.launch {
            repository.insertStamp(
                StampEntity(
                    collectionId = collectionId,
                    imagePath = imagePath,
                    name = name,
                    latitude = lat,
                    longitude = lng
                )
            )
        }
    }

    fun updateStampPosition(stamp: StampEntity, offsetX: Float, offsetY: Float) {
        viewModelScope.launch {
            repository.updateStamp(stamp.copy(offsetX = offsetX, offsetY = offsetY))
        }
    }

    fun updateStampDetails(stamp: StampEntity, name: String, description: String, category: String) {
        viewModelScope.launch {
            repository.updateStamp(stamp.copy(name = name, description = description, category = category))
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        if (category != null) _isFavoriteFilter.value = false
    }

    fun setFavoriteFilter(enabled: Boolean) {
        _isFavoriteFilter.value = enabled
        if (enabled) _selectedCategory.value = null
    }

    fun getStampById(id: Int): Flow<Resource<StampEntity?>> = wrapFlowInResource(repository.getStampById(id))

    fun deleteStamp(stamp: StampEntity) {
        viewModelScope.launch {
            repository.deleteStamp(stamp)
        }
    }

    fun deleteCollection(collection: CollectionEntity) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
        }
    }

    fun toggleFavorite(stamp: StampEntity) {
        viewModelScope.launch {
            repository.updateStamp(stamp.copy(isFavorite = !stamp.isFavorite))
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.insertCategory(CategoryEntity(name = name))
        }
    }

    fun updateCategory(category: CategoryEntity, newName: String) {
        viewModelScope.launch {
            repository.updateStampsCategoryName(category.name, newName)
            repository.updateCategory(category.copy(name = newName))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.clearStampsCategory(category.name)
            repository.deleteCategory(category)
        }
    }

    fun updateCollectionOrder(collections: List<CollectionEntity>) {
        viewModelScope.launch {
            collections.forEachIndexed { index, collection ->
                if (collection.orderIndex != index) {
                    repository.updateCollection(collection.copy(orderIndex = index))
                }
            }
        }
    }
}
