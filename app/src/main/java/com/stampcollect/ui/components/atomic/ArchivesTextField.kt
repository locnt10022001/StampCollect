package com.stampcollect.ui.components.atomic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stampcollect.ui.theme.SurfaceSection
import com.stampcollect.ui.theme.Primary
import com.stampcollect.ui.theme.TextTertiary

/**
 * Standard Styled TextField for the Archival interface.
 */
@Composable
fun ArchivesTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextTertiary) },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceSection,
            unfocusedContainerColor = SurfaceSection,
            cursorColor = Primary,
            focusedIndicatorColor = Primary,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Premium Search Bar component with specialized styling.
 */
@Composable
fun ArchivesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceSection)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    placeholder, 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = TextTertiary
                ) 
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = Primary,
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}
