package com.stampcollect.ui.components.atomic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.stampcollect.util.AtmosphericShadow
import com.stampcollect.util.SilkGradient

/**
 * Signature Floating Action Button with Silk Gradient and Atmospheric Shadow.
 */
@Composable
fun ArchivesFAB(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 48
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = Color.Transparent, // Gradient handled by modifier
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
        modifier = modifier
            .size(size.dp)
            .SilkGradient()
            .AtmosphericShadow()
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size((size * 0.5f).dp))
    }
}

/**
 * Premium rounded button for primary actions.
 */
@Composable
fun ArchivesButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(48.dp).AtmosphericShadow(),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = contentColor)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Styled TextButton for secondary or dismissive actions.
 */
@Composable
fun ArchivesTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text, color = color, style = MaterialTheme.typography.labelLarge)
    }
}
