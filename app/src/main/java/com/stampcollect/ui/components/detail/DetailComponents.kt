package com.stampcollect.ui.components.detail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.stampcollect.R
import com.stampcollect.data.entity.StampEntity
import com.stampcollect.ui.components.atomic.ArchivesTextField
import com.stampcollect.ui.theme.*
import com.stampcollect.util.AtmosphericShadow
import com.stampcollect.util.DateFormat
import java.io.File
import kotlin.math.roundToInt

/**
 * The Archivist's Eye — floating magnifying lens for high-fidelity inspection.
 */
@Composable
fun SpecimenLoupe(file: File, x: Float, y: Float) {
    Box(
        modifier = Modifier
            .offset { IntOffset(x.roundToInt() - 100, y.roundToInt() - 100) }
            .size(160.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .AtmosphericShadow()
            .zIndex(100f)
    ) {
        Image(
            painter = rememberAsyncImagePainter(file),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 3f
                    scaleY = 3f
                }
        )
        // Lens Effect Overlay
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f)))
    }
}

/**
 * Read-only stamp details: name, date, category badge, description, location.
 */
@SuppressLint("DefaultLocale")
@Composable
fun StampViewSection(stamp: StampEntity) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stamp.name.ifBlank { stringResource(R.string.untitled_stamp) },
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary
        )
        Text(
            DateFormat.DEFAULT.format(stamp.timestamp).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        MetadataBadge(stamp.category)

        Spacer(modifier = Modifier.height(48.dp))

        EditorialSection(
            stringResource(R.string.description),
            stamp.description.ifBlank { stringResource(R.string.no_description) }
        )

        if (stamp.latitude != null && stamp.longitude != null) {
            Spacer(modifier = Modifier.height(32.dp))
            EditorialSection(
                stringResource(R.string.captured_at),
                "Coordinates: ${String.format("%.6f", stamp.latitude)}, ${String.format("%.6f", stamp.longitude)}"
            )
        }
    }
}

/**
 * Editable stamp fields: name, description, category chips.
 */
@Composable
fun StampEditSection(
    name: String, onNameChange: (String) -> Unit,
    desc: String, onDescChange: (String) -> Unit,
    category: String, onCategoryChange: (String) -> Unit,
    categories: List<String>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.basic_info).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        ArchivesTextField(value = name, onValueChange = onNameChange, placeholder = stringResource(R.string.stamp_name))
        Spacer(modifier = Modifier.height(24.dp))
        ArchivesTextField(value = desc, onValueChange = onDescChange, placeholder = stringResource(R.string.description))
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(R.string.category).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(categories.size) { index ->
                val cat = categories[index]
                FilterChip(
                    selected = category == cat,
                    onClick = { onCategoryChange(cat) },
                    label = { Text(cat, style = MaterialTheme.typography.labelLarge) },
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

/**
 * Tonal category badge.
 */
@Composable
fun MetadataBadge(category: String) {
    Surface(color = SurfaceSection, shape = CircleShape) {
        Text(
            category.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * Editorial-style section with label and content.
 */
@Composable
fun EditorialSection(label: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
    }
}
