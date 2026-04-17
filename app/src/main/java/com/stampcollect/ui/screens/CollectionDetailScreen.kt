package com.stampcollect.ui.screens

import android.graphics.Picture
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.stampcollect.R
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.ui.theme.*
import com.stampcollect.util.AtmosphericShadow
import com.stampcollect.ui.viewmodel.CollectionViewModel
import com.stampcollect.util.EntranceAnimation
import com.stampcollect.util.GlassMorphic
import com.stampcollect.util.StampShareHelper
import com.stampcollect.ui.components.atomic.ConfirmationDialog
import java.io.File
import kotlin.math.roundToInt

@Composable
fun CollectionDetailScreen(
    collectionId: Int,
    collectionName: String,
    onBackClick: () -> Unit,
    onAddStampClick: () -> Unit,
    onStampClick: (Int) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val stampsRes by viewModel.currentCollectionStamps.collectAsState()
    val stamps = stampsRes.data() ?: emptyList()
    var isGridMode by remember { mutableStateOf(true) }
    var stampToDelete by remember { mutableStateOf<StampEntity?>(null) }

    val context = LocalContext.current
    val picture = remember { Picture() }

    val collectionsRes by viewModel.collections.collectAsState()
    val collections = collectionsRes.data() ?: emptyList()
    val collection = collections.find { it.id == collectionId }
    var showThemePicker by remember { mutableStateOf(false) }

    LaunchedEffect(collectionId) { viewModel.loadStampsForCollection(collectionId) }

    val bgColor = when (collection?.backgroundType) {
        1 -> StampPaperGrid
        2 -> StampPaperCork
        3 -> StampPaperVelvet
        4 -> StampPaperLinen
        else -> StampPaper
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 80.dp) // Space for Glass Header
        ) {
            // Theme Picker (Expansion)
            if (showThemePicker && collection != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().AtmosphericShadow().zIndex(5f), 
                    color = GlassWhite,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            stringResource(R.string.background).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "Original" to 0, "Grid" to 1, "Cork" to 2, "Velvet" to 3, "Linen" to 4
                            ).forEach { (label, type) ->
                                FilterChip(
                                    selected = collection.backgroundType == type,
                                    onClick = { viewModel.updateCollectionBackground(collection, type) },
                                    label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                                    shape = CircleShape,
                                    border = null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = SurfaceSection,
                                        selectedContainerColor = Primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            val contentModifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        val pictureCanvas = Canvas(picture.beginRecording(size.width.toInt(), size.height.toInt()))
                        drawIntoCanvas { canvas ->
                            pictureCanvas.nativeCanvas.drawColor(bgColor.toArgb())
                            drawContent()
                        }
                        picture.endRecording()
                        drawContent()
                    }
                }

            if (stamps.isEmpty()) {
                Box(
                    modifier = contentModifier, contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp).EntranceAnimation()) {
                        Text(
                            "A sanctuary for legacies yet to be curated.",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Your collection chronicles begin here.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (isGridMode) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = contentModifier
                ) {
                    itemsIndexed(stamps) { index, stamp ->
                        Surface(
                            modifier = Modifier
                                .aspectRatio(0.8f)
                                .EntranceAnimation(delay = index * 50)
                                .AtmosphericShadow()
                                .clickable { onStampClick(stamp.id) },
                            shape = RoundedCornerShape(24.dp),
                            color = SurfaceCard
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val file = File(stamp.imagePath)
                                if (file.exists()) {
                                    Box(modifier = Modifier.fillMaxSize().padding(12.dp).background(TertiaryFixed)) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = Uri.fromFile(file)),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }

                                if (stamp.isFavorite) {
                                    Icon(
                                        Icons.Default.Favorite, null, tint = Secondary,
                                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { stampToDelete = stamp },
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).size(32.dp)
                                ) { Icon(Icons.Default.Delete, null, tint = Secondary.copy(alpha = 0.6f), modifier = Modifier.size(16.dp)) }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            } else {
                // Scrapbook Mode — No-Line refinements
                Box(modifier = contentModifier) {
                    stamps.forEach { stamp ->
                        var offsetX by remember(stamp.id) { mutableFloatStateOf(stamp.offsetX) }
                        var offsetY by remember(stamp.id) { mutableFloatStateOf(stamp.offsetY) }

                        Surface(
                            modifier = Modifier
                                .width(130.dp)
                                .aspectRatio(0.8f)
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .EntranceAnimation(delay = 100)
                                .AtmosphericShadow()
                                .pointerInput(stamp.id) {
                                    detectDragGestures(onDragEnd = { viewModel.updateStampPosition(stamp, offsetX, offsetY) }) { change, dragAmount ->
                                        change.consume(); offsetX += dragAmount.x; offsetY += dragAmount.y
                                    }
                                }
                                .clickable { onStampClick(stamp.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceCard
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val file = File(stamp.imagePath)
                                if (file.exists()) {
                                    Box(modifier = Modifier.fillMaxSize().padding(8.dp).background(TertiaryFixed)) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = Uri.fromFile(file)),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                if (stamp.isFavorite) {
                                    Icon(Icons.Default.Favorite, null, tint = Secondary, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(14.dp))
                                }
                                IconButton(
                                    onClick = { stampToDelete = stamp },
                                    modifier = Modifier.align(Alignment.BottomEnd).size(28.dp)
                                ) { Icon(Icons.Default.Delete, null, tint = Secondary.copy(alpha = 0.6f), modifier = Modifier.size(14.dp)) }
                            }
                        }
                    }
                }
            }
        }

        // Glass Header
        DetailHeader(
            collectionName = collectionName,
            isGridMode = isGridMode,
            showThemePicker = showThemePicker,
            onBackClick = onBackClick,
            onThemeToggle = { showThemePicker = !showThemePicker },
            onGridToggle = { isGridMode = !isGridMode },
            onShare = {
                val bitmap = android.graphics.Bitmap.createBitmap(picture.width.coerceAtLeast(1), picture.height.coerceAtLeast(1), android.graphics.Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)
                canvas.drawPicture(picture)
                StampShareHelper.shareBitmap(context, bitmap, "collection_${collectionName}.png")
            }
        )
    }

    if (stampToDelete != null) {
        ConfirmationDialog(
            title = stringResource(R.string.delete_stamp),
            message = stringResource(R.string.delete_stamp_message),
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { viewModel.deleteStamp(stampToDelete!!); stampToDelete = null },
            onDismiss = { stampToDelete = null }
        )
    }
}

@Composable
fun DetailHeader(
    collectionName: String,
    isGridMode: Boolean,
    showThemePicker: Boolean,
    onBackClick: () -> Unit,
    onThemeToggle: () -> Unit,
    onGridToggle: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary) }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    collectionName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row {
                IconButton(onClick = onThemeToggle) { Icon(Icons.Default.Palette, null, tint = if (showThemePicker) Primary else TextSecondary) }
                IconButton(onClick = onShare) { Icon(Icons.Default.Share, null, tint = Primary) }
                IconButton(onClick = onGridToggle) { Icon(if (isGridMode) Icons.Filled.Wallpaper else Icons.Filled.GridView, null, tint = Primary) }
            }
        }
    }
}
