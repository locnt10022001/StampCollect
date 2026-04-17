package com.stampcollect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stampcollect.data.entity.CategoryEntity
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.ui.components.home.*
import com.stampcollect.ui.theme.*
import com.stampcollect.ui.viewmodel.CollectionViewModel
import com.stampcollect.ui.components.atomic.ArchivesSearchBar
import com.stampcollect.R
import androidx.compose.ui.res.stringResource

@Composable
fun HomeScreen(
    onCollectionClick: (Int, String) -> Unit,
    onStampClick: (Int) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    // Core Data State
    val collectionsRes by viewModel.collections.collectAsState()
    val collections = collectionsRes.data() ?: emptyList()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredStampsRes by viewModel.filteredStamps.collectAsState()
    val filteredStamps = filteredStampsRes.data() ?: emptyList()
    val allStampsRes by viewModel.allStamps.collectAsState()
    val allStamps = allStampsRes.data() ?: emptyList()
    val categoriesRes by viewModel.categories.collectAsState()
    val categories = categoriesRes.data() ?: emptyList()
    val isFavoriteFilter by viewModel.isFavoriteFilter.collectAsState()

    // Dialog & UI Interaction State
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCollection by remember { mutableStateOf<CollectionEntity?>(null) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Any?>(null) }
    var longPressTarget by remember { mutableStateOf<Any?>(null) }
    var showUnsavedChangesDialog by remember { mutableStateOf<(() -> Unit)?>(null) }
    var confirmActionState by remember { mutableStateOf<(() -> Unit)?>(null) }
    var confirmTitle by remember { mutableStateOf("") }
    var confirmMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(BgPrimary)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 80.dp)
        ) {
            ArchivesSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                placeholder = stringResource(R.string.search_stamps)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CategoryFilterBar(
                categories = categories,
                selectedCategory = selectedCategory,
                isFavoriteFilter = isFavoriteFilter,
                onCategoryClick = { categoryName -> 
                    viewModel.setCategory(if (selectedCategory == categoryName) null else categoryName)
                },
                onFavoriteClick = { viewModel.setFavoriteFilter(!isFavoriteFilter) },
                onAddCategoryClick = { showAddCategoryDialog = true },
                onLongPressCategory = { longPressTarget = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                if (searchQuery.isNotEmpty() || selectedCategory != null || isFavoriteFilter) {
                    FilteredStampGrid(
                        stamps = filteredStamps,
                        onStampClick = onStampClick,
                        onLongPressStamp = { longPressTarget = it }
                    )
                } else if (collectionsRes.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (collections.isEmpty()) {
                    EmptyCollectionsView()
                } else {
                    CollectionListView(
                        collections = collections,
                        allStamps = allStamps,
                        onCollectionClick = onCollectionClick,
                        onEditCollection = { editingCollection = it },
                        onDeleteCollection = { deleteTarget = it },
                        onOrderUpdate = { viewModel.updateCollectionOrder(it) }
                    )
                }
            }
        }

        HomeHeader(
            onAddClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    HomeDialogManager(
        showAddDialog = showAddDialog,
        editingCollection = editingCollection,
        editingCategory = editingCategory,
        showAddCategoryDialog = showAddCategoryDialog,
        deleteTarget = deleteTarget,
        longPressTarget = longPressTarget,
        showUnsavedChangesDialog = showUnsavedChangesDialog,
        confirmActionState = confirmActionState,
        confirmTitle = confirmTitle,
        confirmMessage = confirmMessage,
        viewModel = viewModel,
        onStampClick = onStampClick,
        onDismissAdd = { showAddDialog = false },
        onDismissEditCollection = { editingCollection = null },
        onDismissEditCategory = { editingCategory = null },
        onDismissAddCategory = { showAddCategoryDialog = false },
        onDismissDelete = { deleteTarget = null },
        onDismissLongPress = { longPressTarget = null },
        onDismissUnsaved = { showUnsavedChangesDialog = null },
        onDismissConfirm = { confirmActionState = null },
        setConfirm = { title, msg, action -> 
            confirmTitle = title
            confirmMessage = msg
            confirmActionState = action
        },
        setShowUnsavedChanges = { showUnsavedChangesDialog = it },
        setEditingCollection = { editingCollection = it },
        setEditingCategory = { editingCategory = it },
        setDeleteTarget = { deleteTarget = it }
    )
}
