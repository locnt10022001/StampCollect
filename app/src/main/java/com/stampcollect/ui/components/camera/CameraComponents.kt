package com.stampcollect.ui.components.camera

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.stampcollect.R
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.ui.theme.*
import com.stampcollect.util.AtmosphericShadow
import java.io.File

/**
 * Top control bar with collection and frame type selectors.
 */
@Composable
fun CameraTopControls(
    selectedCollectionName: String?,
    frameType: String,
    expanded: Boolean,
    frameExpanded: Boolean,
    collections: List<CollectionEntity>,
    onCollectionExpandToggle: () -> Unit,
    onFrameExpandToggle: () -> Unit,
    onCollectionSelect: (CollectionEntity) -> Unit,
    onFrameSelect: (String) -> Unit,
    onDismissCollection: () -> Unit,
    onDismissFrame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 56.dp).padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Collection Selector
        Surface(shape = CircleShape, color = GlassDark, modifier = Modifier.clickable { onCollectionExpandToggle() }) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(selectedCollectionName?.uppercase() ?: stringResource(R.string.all), color = Color.White, style = MaterialTheme.typography.labelSmall)
                Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
        }
        // Frame Type Selector
        Surface(shape = CircleShape, color = GlassDark, modifier = Modifier.clickable { onFrameExpandToggle() }) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                val frameLabel = when(frameType) {
                    "Classic" -> stringResource(R.string.classic).uppercase()
                    "Scalloped" -> stringResource(R.string.scalloped).uppercase()
                    "Modern" -> stringResource(R.string.modern).uppercase()
                    else -> frameType.uppercase()
                }
                Text(frameLabel, color = Color.White, style = MaterialTheme.typography.labelSmall)
                Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = onDismissCollection) {
            collections.forEach { c ->
                DropdownMenuItem(text = { Text(c.name) }, onClick = { onCollectionSelect(c); })
            }
        }
        DropdownMenu(expanded = frameExpanded, onDismissRequest = onDismissFrame) {
            listOf("Classic", "Scalloped", "Modern").forEach { type ->
                DropdownMenuItem(text = { Text(type) }, onClick = { onFrameSelect(type) })
            }
        }
    }
}

/**
 * Bottom action center with shutter, flip camera, and gallery buttons.
 */
@Composable
fun CameraActionCenter(
    onShutterClick: () -> Unit,
    onFlipCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Shutter Button
        Surface(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable { onShutterClick() },
            color = Color.Transparent,
            border = BorderStroke(4.dp, Color.White),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = Color.White) {}
            }
        }

        // Flip Camera
        IconButton(
            onClick = onFlipCamera,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 40.dp)
        ) { Icon(Icons.Default.Autorenew, null, tint = Color.White, modifier = Modifier.size(32.dp)) }

        // Gallery
        IconButton(
            onClick = onOpenGallery,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 40.dp)
        ) { Icon(Icons.Default.Collections, null, tint = Color.White, modifier = Modifier.size(32.dp)) }
    }
}

/**
 * Photo preview overlay shown after capture, with name input and save/discard actions.
 */
@Composable
fun PhotoPreviewOverlay(
    photoUri: String,
    stampName: String,
    onStampNameChange: (String) -> Unit,
    startSaveAnimation: Boolean,
    transX: Float,
    transY: Float,
    previewScale: Float,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
    canSave: Boolean
) {
    Box(modifier = Modifier.fillMaxSize().background(BgOverlay), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            val file = File(photoUri)
            if (file.exists()) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(0.77f)
                        .graphicsLayer {
                            translationX = transX
                            translationY = transY
                            scaleX = previewScale
                            scaleY = previewScale
                        }
                        .AtmosphericShadow(),
                    color = TertiaryFixed
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(Uri.fromFile(file)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    )
                }
            }
            if (!startSaveAnimation) {
                Spacer(modifier = Modifier.height(32.dp))

                TextField(
                    value = stampName,
                    onValueChange = onStampNameChange,
                    placeholder = { Text(stringResource(R.string.name_your_stamp).uppercase(), color = TextTertiary, style = MaterialTheme.typography.labelSmall) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        cursorColor = Color.White, focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onDiscard,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Secondary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Secondary)
                    ) { Text(stringResource(R.string.discard).uppercase(), style = MaterialTheme.typography.labelLarge) }

                    Button(
                        onClick = { if (canSave) onSave() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) { Text(stringResource(R.string.save).uppercase(), color = Color.White, style = MaterialTheme.typography.labelLarge) }
                }
            }
        }
    }
}
