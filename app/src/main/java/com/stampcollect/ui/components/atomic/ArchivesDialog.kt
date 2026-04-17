package com.stampcollect.ui.components.atomic

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stampcollect.ui.theme.SurfaceCard
import com.stampcollect.ui.theme.TextPrimary
import com.stampcollect.ui.theme.TextSecondary
import com.stampcollect.ui.theme.Secondary

/**
 * Standard confirmation dialog for destructive or important actions.
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color = Secondary
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(24.dp),
        title = { 
            Text(
                title, 
                color = TextPrimary, 
                style = MaterialTheme.typography.headlineLarge
            ) 
        },
        text = { 
            Text(
                message, 
                style = MaterialTheme.typography.bodyLarge, 
                color = TextSecondary
            ) 
        },
        confirmButton = {
            ArchivesButton(
                text = confirmText,
                onClick = onConfirm,
                containerColor = confirmColor,
                modifier = androidx.compose.ui.Modifier // Use root modifier for sizing control
            )
        },
        dismissButton = {
            ArchivesTextButton(
                text = dismissText,
                onClick = onDismiss
            )
        }
    )
}

/**
 * Base Styled AlertDialog wrapper for custom content.
 */
@Composable
fun ArchivesDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(24.dp),
        title = { 
            Text(
                title, 
                style = MaterialTheme.typography.headlineLarge, 
                color = TextPrimary
            ) 
        },
        text = content,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}
