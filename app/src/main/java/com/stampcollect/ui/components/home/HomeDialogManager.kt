package com.stampcollect.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stampcollect.R
import com.stampcollect.data.entity.CategoryEntity
import com.stampcollect.data.entity.CollectionEntity
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.ui.components.atomic.ArchivesButton
import com.stampcollect.ui.components.atomic.ArchivesDialog
import com.stampcollect.ui.components.atomic.ArchivesTextButton
import com.stampcollect.ui.components.atomic.ArchivesTextField
import com.stampcollect.ui.components.atomic.ConfirmationDialog
import com.stampcollect.ui.theme.*
import com.stampcollect.ui.viewmodel.CollectionViewModel
import com.stampcollect.util.AtmosphericShadow

@Composable
fun HomeDialogManager(
    showAddDialog: Boolean,
    editingCollection: CollectionEntity?,
    editingCategory: CategoryEntity?,
    showAddCategoryDialog: Boolean,
    deleteTarget: Any?,
    longPressTarget: Any?,
    showUnsavedChangesDialog: (() -> Unit)?,
    confirmActionState: (() -> Unit)?,
    confirmTitle: String,
    confirmMessage: String,
    viewModel: CollectionViewModel,
    onStampClick: (Int) -> Unit,
    onDismissAdd: () -> Unit,
    onDismissEditCollection: () -> Unit,
    onDismissEditCategory: () -> Unit,
    onDismissAddCategory: () -> Unit,
    onDismissDelete: () -> Unit,
    onDismissLongPress: () -> Unit,
    onDismissUnsaved: () -> Unit,
    onDismissConfirm: () -> Unit,
    setConfirm: (String, String, () -> Unit) -> Unit,
    setShowUnsavedChanges: ((() -> Unit)?) -> Unit,
    setEditingCollection: (CollectionEntity?) -> Unit,
    setEditingCategory: (CategoryEntity?) -> Unit,
    setDeleteTarget: (Any?) -> Unit
) {
    val context = LocalContext.current

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        
        ArchivesDialog(
            onDismissRequest = onDismissAdd,
            title = stringResource(R.string.new_collection),
            confirmButton = {
                ArchivesButton(
                    text = stringResource(R.string.create),
                    onClick = {
                        if (name.isNotBlank()) {
                            setConfirm(
                                context.getString(R.string.create_collection),
                                context.getString(R.string.create_collection_confirm)
                            ) { viewModel.addCollection(name, desc); onDismissAdd() }
                        }
                    }
                )
            },
            dismissButton = {
                ArchivesTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = {
                        if (name.isNotBlank() || desc.isNotBlank()) {
                            setShowUnsavedChanges { onDismissAdd() }
                        } else onDismissAdd()
                    }
                )
            }
        ) {
            Column {
                ArchivesTextField(value = name, onValueChange = { name = it }, placeholder = stringResource(R.string.name))
                Spacer(modifier = Modifier.height(16.dp))
                ArchivesTextField(value = desc, onValueChange = { desc = it }, placeholder = stringResource(R.string.description))
            }
        }
    }

    if (editingCollection != null) {
        var name by remember(editingCollection) { mutableStateOf(editingCollection.name) }
        var desc by remember(editingCollection) { mutableStateOf(editingCollection.description) }
        
        ArchivesDialog(
            onDismissRequest = onDismissEditCollection,
            title = stringResource(R.string.edit_collection),
            confirmButton = {
                ArchivesButton(
                    text = stringResource(R.string.save),
                    onClick = {
                        if (name.isNotBlank()) {
                            setConfirm(
                                context.getString(R.string.save_changes),
                                context.getString(R.string.save_changes_confirm)
                            ) { viewModel.updateCollection(editingCollection.copy(name = name, description = desc)); onDismissEditCollection() }
                        }
                    }
                )
            },
            dismissButton = {
                ArchivesTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = {
                        if (name != (editingCollection.name) || desc != (editingCollection.description)) {
                            setShowUnsavedChanges { onDismissEditCollection() }
                        } else onDismissEditCollection()
                    }
                )
            }
        ) {
            Column {
                ArchivesTextField(value = name, onValueChange = { name = it }, placeholder = stringResource(R.string.name))
                Spacer(modifier = Modifier.height(16.dp))
                ArchivesTextField(value = desc, onValueChange = { desc = it }, placeholder = stringResource(R.string.description))
            }
        }
    }

    if (showAddCategoryDialog) {
        var name by remember { mutableStateOf("") }
        ArchivesDialog(
            onDismissRequest = onDismissAddCategory,
            title = stringResource(R.string.new_category),
            confirmButton = {
                ArchivesButton(
                    text = stringResource(R.string.add),
                    onClick = {
                        if (name.isNotBlank()) {
                            setConfirm(
                                context.getString(R.string.add_category),
                                context.getString(R.string.add_category_confirm)
                            ) { viewModel.addCategory(name); onDismissAddCategory() }
                        }
                    }
                )
            },
            dismissButton = {
                ArchivesTextButton(onClick = onDismissAddCategory, text = stringResource(R.string.cancel))
            }
        ) { 
            ArchivesTextField(value = name, onValueChange = { name = it }, placeholder = stringResource(R.string.category_name)) 
        }
    }

    if (editingCategory != null) {
        var name by remember(editingCategory) { mutableStateOf(editingCategory.name) }
        ArchivesDialog(
            onDismissRequest = onDismissEditCategory,
            title = stringResource(R.string.edit_category),
            confirmButton = {
                ArchivesButton(
                    text = stringResource(R.string.save),
                    onClick = {
                        if (name.isNotBlank()) {
                            setConfirm(
                                context.getString(R.string.save_changes),
                                context.getString(R.string.save_changes_confirm)
                            ) { viewModel.updateCategory(editingCategory, name); onDismissEditCategory() }
                        }
                    }
                )
            },
            dismissButton = {
                ArchivesTextButton(onClick = onDismissEditCategory, text = stringResource(R.string.cancel))
            }
        ) { 
            ArchivesTextField(value = name, onValueChange = { name = it }, placeholder = stringResource(R.string.category_name)) 
        }
    }

    if (deleteTarget != null) {
        val title = when (deleteTarget) {
            is CollectionEntity -> stringResource(R.string.delete_collection)
            is CategoryEntity -> stringResource(R.string.delete_category)
            is StampEntity -> stringResource(R.string.delete_stamp)
            else -> stringResource(R.string.delete_item)
        }
        val msg = when (deleteTarget) {
            is CollectionEntity -> stringResource(R.string.delete_collection_message)
            is CategoryEntity -> stringResource(R.string.delete_category_message)
            else -> stringResource(R.string.delete_item_message)
        }
        ConfirmationDialog(
            title = title,
            message = msg,
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                when (deleteTarget) {
                    is CollectionEntity -> viewModel.deleteCollection(deleteTarget)
                    is CategoryEntity -> viewModel.deleteCategory(deleteTarget)
                    is StampEntity -> viewModel.deleteStamp(deleteTarget)
                }
                onDismissDelete()
            },
            onDismiss = onDismissDelete
        )
    }

    if (longPressTarget != null) {
        ArchivesDialog(
            onDismissRequest = onDismissLongPress,
            title = stringResource(R.string.options),
            confirmButton = {},
            dismissButton = { ArchivesTextButton(onClick = onDismissLongPress, text = stringResource(R.string.cancel)) }
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionButton(Icons.Default.Edit, stringResource(R.string.edit), Primary) {
                    val target = longPressTarget
                    onDismissLongPress()
                    if (target is CategoryEntity) setEditingCategory(target)
                    else if (target is StampEntity) onStampClick(target.id)
                }
                OptionButton(Icons.Default.Delete, stringResource(R.string.delete), Secondary) {
                    setDeleteTarget(longPressTarget)
                    onDismissLongPress()
                }
            }
        }
    }

    if (showUnsavedChangesDialog != null) {
        ConfirmationDialog(
            title = stringResource(R.string.unsaved_changes),
            message = stringResource(R.string.unsaved_changes_message),
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { showUnsavedChangesDialog.invoke(); onDismissUnsaved() },
            onDismiss = onDismissUnsaved
        )
    }

    if (confirmActionState != null) {
        ConfirmationDialog(
            title = confirmTitle,
            message = confirmMessage,
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { confirmActionState.invoke(); onDismissConfirm() },
            onDismiss = onDismissConfirm
        )
    }
}

@Composable
fun OptionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().AtmosphericShadow(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}
