package com.stampcollect.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.stampcollect.R
import com.stampcollect.ui.components.atomic.ConfirmationDialog
import com.stampcollect.ui.components.detail.*
import com.stampcollect.ui.theme.*
import com.stampcollect.ui.viewmodel.CollectionViewModel
import com.stampcollect.util.AtmosphericShadow
import com.stampcollect.util.EntranceAnimation
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StampDetailScreen(
    stampId: Int,
    onBackClick: () -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val stampFlow = remember(stampId) { viewModel.getStampById(stampId) }
    val stampRes by stampFlow.collectAsState(initial = null)
    val stamp = stampRes?.data()
    var isEditMode by remember { mutableStateOf(false) }

    // Edit states
    var editName by remember(stamp) { mutableStateOf(stamp?.name ?: "") }
    var editDesc by remember(stamp) { mutableStateOf(stamp?.description ?: "") }
    var editCategory by remember(stamp) { mutableStateOf(stamp?.category ?: "Flora") }

    val categoriesRes by viewModel.categories.collectAsState()
    val categories = categoriesRes.data() ?: emptyList()

    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showConfirmSaveDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(BgPrimary)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (isEditMode) stringResource(R.string.edit_stamp) else "",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = TextPrimary)
                        }
                    },
                    actions = {
                        if (stamp != null) {
                            if (!isEditMode) {
                                IconButton(onClick = { viewModel.toggleFavorite(stamp!!) }) {
                                    Icon(
                                        imageVector = if (stamp!!.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (stamp!!.isFavorite) Secondary else TextSecondary
                                    )
                                }
                            }
                            if (isEditMode) {
                                IconButton(onClick = {
                                    if (editName != (stamp?.name ?: "") || editDesc != (stamp?.description ?: "") || editCategory != (stamp?.category ?: "")) {
                                        showUnsavedChangesDialog = true
                                    } else { isEditMode = false }
                                }) {
                                    Icon(Icons.Default.Close, stringResource(R.string.cancel), tint = TextSecondary)
                                }
                                IconButton(onClick = { showConfirmSaveDialog = true }) {
                                    Icon(Icons.Default.Save, stringResource(R.string.save), tint = Primary)
                                }
                            } else {
                                IconButton(onClick = { isEditMode = true }) {
                                    Icon(Icons.Default.Edit, stringResource(R.string.edit_collection), tint = Primary)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (stamp == null) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                val file = File(stamp!!.imagePath)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Stamp Specimen Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                            .EntranceAnimation()
                    ) {
                        // Background Mount
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(0.9f)
                                .align(Alignment.CenterEnd)
                                .clip(RoundedCornerShape(topStart = 40.dp, bottomStart = 40.dp))
                                .background(TertiaryFixed)
                        )

                        // Float Stamp with Loupe
                        var isMagnifying by remember { mutableStateOf(false) }
                        var touchX by remember { mutableStateOf(0f) }
                        var touchY by remember { mutableStateOf(0f) }

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .padding(start = 24.dp, top = 40.dp, bottom = 40.dp)
                                .fillMaxWidth(0.85f)
                                .aspectRatio(0.8f)
                                .AtmosphericShadow()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = { offset ->
                                            isMagnifying = true
                                            touchX = offset.x
                                            touchY = offset.y
                                            tryAwaitRelease()
                                            isMagnifying = false
                                        }
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                if (file.exists()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(Uri.fromFile(file)),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        if (isMagnifying && file.exists()) {
                            SpecimenLoupe(file, touchX, touchY)
                        }
                    }

                    // Content Section
                    Column(modifier = Modifier.padding(horizontal = 24.dp).EntranceAnimation(delay = 200)) {
                        if (isEditMode) {
                            StampEditSection(
                                name = editName,
                                onNameChange = { editName = it },
                                desc = editDesc,
                                onDescChange = { editDesc = it },
                                category = editCategory,
                                onCategoryChange = { editCategory = it },
                                categories = categories.map { it.name }
                            )
                        } else {
                            StampViewSection(stamp)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Dialogs
        if (showConfirmSaveDialog) {
            ConfirmationDialog(
                title = stringResource(R.string.save_changes),
                message = stringResource(R.string.save_changes_confirm),
                confirmText = stringResource(R.string.save),
                dismissText = stringResource(R.string.cancel),
                confirmColor = Primary,
                onConfirm = {
                    viewModel.updateStampDetails(stamp!!, editName, editDesc, editCategory)
                    isEditMode = false
                    showConfirmSaveDialog = false
                },
                onDismiss = { showConfirmSaveDialog = false }
            )
        }

        if (showUnsavedChangesDialog) {
            ConfirmationDialog(
                title = stringResource(R.string.unsaved_changes),
                message = stringResource(R.string.unsaved_changes_message),
                confirmText = stringResource(R.string.confirm),
                dismissText = stringResource(R.string.cancel),
                onConfirm = {
                    showUnsavedChangesDialog = false
                    isEditMode = false
                    editName = stamp?.name ?: ""
                    editDesc = stamp?.description ?: ""
                    editCategory = stamp?.category ?: ""
                },
                onDismiss = { showUnsavedChangesDialog = false }
            )
        }
    }
}
