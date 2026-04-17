package com.stampcollect.ui.components.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.stampcollect.R
import com.stampcollect.data.entity.CategoryEntity
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.ui.components.atomic.ArchivesFAB
import com.stampcollect.ui.components.atomic.ArchivesSearchBar
import com.stampcollect.ui.theme.*
import com.stampcollect.util.*
import java.io.File
import kotlin.math.roundToInt

@Composable
fun HomeHeader(onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(80.dp)
            .GlassMorphic()
            .zIndex(10f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.collections),
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary
            )
            ArchivesFAB(
                icon = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.add),
                onClick = onAddClick
            )
        }
    }
}

@Composable
fun CategoryFilterBar(
    categories: List<CategoryEntity>,
    selectedCategory: String?,
    isFavoriteFilter: Boolean,
    onCategoryClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onLongPressCategory: (CategoryEntity) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            FilterChip(
                selected = !isFavoriteFilter && selectedCategory == null,
                onClick = { onCategoryClick("") },
                label = { Text(stringResource(R.string.all), style = MaterialTheme.typography.labelLarge) },
                shape = CircleShape,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = SurfaceSection,
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White,
                    labelColor = TextSecondary
                )
            )
        }
        item {
            FilterChip(
                selected = isFavoriteFilter,
                onClick = onFavoriteClick,
                label = { Text(stringResource(R.string.favorites_tab), style = MaterialTheme.typography.labelLarge) },
                shape = CircleShape,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = SurfaceSection,
                    selectedContainerColor = Secondary,
                    selectedLabelColor = Color.White,
                    labelColor = TextSecondary
                )
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = !isFavoriteFilter && selectedCategory == category.name,
                onClick = { onCategoryClick(category.name) },
                label = {
                    Text(category.name, style = MaterialTheme.typography.labelLarge, modifier = Modifier.pointerInput(category.id) {
                        detectTapGestures(
                            onTap = { onCategoryClick(category.name) },
                            onLongPress = { onLongPressCategory(category) }
                        )
                    })
                },
                shape = CircleShape,
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = SurfaceSection,
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White,
                    labelColor = TextSecondary
                )
            )
        }
        item {
            IconButton(onClick = onAddCategoryClick, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Rounded.Add, stringResource(R.string.add_category), tint = Primary)
            }
        }
    }
}

@Composable
fun FilteredStampGrid(
    stamps: List<StampEntity>,
    onStampClick: (Int) -> Unit,
    onLongPressStamp: (StampEntity) -> Unit
) {
    if (stamps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_stamps_found), style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stamps) { stamp ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SurfaceCard,
                    modifier = Modifier
                        .EntranceAnimation(delay = 100)
                        .AtmosphericShadow()
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.8f)
                            .pointerInput(stamp.id) {
                                detectTapGestures(
                                    onTap = { onStampClick(stamp.id) },
                                    onLongPress = { onLongPressStamp(stamp) }
                                )
                            }
                    ) {
                        val file = File(stamp.imagePath)
                        if (file.exists()) {
                            coil.compose.AsyncImage(
                                model = file,
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(12.dp)
                            )
                        }
                        if (stamp.isFavorite) {
                            Icon(
                                Icons.Default.Favorite, null, tint = Secondary,
                                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(20.dp)
                            )
                        }
                    }
                }
            }
            item(span = { GridItemSpan(3) }) { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }
}

@Composable
fun CollectionListView(
    collections: List<CollectionEntity>,
    allStamps: List<StampEntity>,
    onCollectionClick: (Int, String) -> Unit,
    onEditCollection: (CollectionEntity) -> Unit,
    onDeleteCollection: (CollectionEntity) -> Unit,
    onOrderUpdate: (List<CollectionEntity>) -> Unit
) {
    val listState = rememberLazyListState()
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val localCollections = remember(collections) { collections.toMutableStateList() }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(localCollections, key = { _, col -> col.id }) { index, collection ->
            val miniatures = remember(allStamps, collection.id) {
                allStamps.filter { it.collectionId == collection.id }.take(4)
            }

            val isDragging = draggedItemIndex == index
            val zIndex = if (isDragging) 1f else 0f
            val displacement = if (isDragging) dragOffset else 0f

            Box(
                modifier = Modifier
                    .zIndex(zIndex)
                    .offset { IntOffset(0, displacement.roundToInt()) }
                    .EntranceAnimation(delay = index * 50)
                    .pointerInput(index) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { draggedItemIndex = index },
                            onDragEnd = {
                                draggedItemIndex = null
                                dragOffset = 0f
                                onOrderUpdate(localCollections.toList())
                            },
                            onDragCancel = {
                                draggedItemIndex = null
                                dragOffset = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount.y
                                val targetIndex = if (dragOffset > 50 && index < localCollections.size - 1) index + 1
                                                 else if (dragOffset < -50 && index > 0) index - 1
                                                 else index
                                
                                if (targetIndex != index && targetIndex != draggedItemIndex) {
                                    localCollections.add(targetIndex, localCollections.removeAt(index))
                                    draggedItemIndex = targetIndex
                                    dragOffset = 0f
                                }
                            }
                        )
                    }
            ) {
                CollectionItem(
                    collection = collection,
                    miniatures = miniatures,
                    isDragging = isDragging,
                    onClick = { onCollectionClick(collection.id, collection.name) },
                    onDelete = { onDeleteCollection(collection) },
                    onEdit = { onEditCollection(collection) },
                    isEditable = collection.name != "All"
                )
            }
        }
        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

@Composable
fun CollectionItem(
    collection: CollectionEntity,
    miniatures: List<StampEntity>,
    isDragging: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isEditable: Boolean = true
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = if (isDragging) 0.8f else 1f
                scaleX = if (isDragging) 1.05f else 1f
                scaleY = if (isDragging) 1.05f else 1f
            }
            .clickable { onClick() }
            .AtmosphericShadow(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceCard
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Menu, null, tint = TextTertiary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(collection.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (collection.description.isNotBlank()) {
                    Text(collection.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (miniatures.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (stamp in miniatures) {
                            val file = File(stamp.imagePath)
                            if (file.exists()) {
                                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(TertiaryFixed)) {
                                    coil.compose.AsyncImage(model = file, contentDescription = null, contentScale = androidx.compose.ui.layout.ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(4.dp))
                                }
                            }
                        }
                    }
                }
            }
            if (isEditable) {
                Column {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Primary, modifier = Modifier.size(22.dp)) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Secondary, modifier = Modifier.size(22.dp)) }
                }
            }
        }
    }
}

@Composable
fun EmptyCollectionsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp).EntranceAnimation()) {
            Text(
                "The archive awaits its first specimen.",
                style = MaterialTheme.typography.displayMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Begin your chronicle by curating a new collection.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
