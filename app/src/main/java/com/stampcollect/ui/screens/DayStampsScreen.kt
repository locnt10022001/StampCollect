package com.stampcollect.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.stampcollect.ui.theme.*
import com.stampcollect.util.AtmosphericShadow
import com.stampcollect.ui.viewmodel.CollectionViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DayStampsScreen(
    timestamp: Long,
    onBackClick: () -> Unit,
    onStampClick: (Int) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val allStampsRes by viewModel.allStamps.collectAsState()
    val allStamps = allStampsRes.data() ?: emptyList()
    val dayStamps = remember(allStamps, timestamp) {
        val calTarget = Calendar.getInstance().apply { timeInMillis = timestamp }
        val sc = Calendar.getInstance()
        allStamps.filter {
            sc.timeInMillis = it.timestamp
            sc.get(Calendar.YEAR) == calTarget.get(Calendar.YEAR) &&
            sc.get(Calendar.DAY_OF_YEAR) == calTarget.get(Calendar.DAY_OF_YEAR)
        }
    }

    val dateStr = remember(timestamp) {
        // Newsreader DISPLAY style usually fits full date names
        SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date(timestamp))
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgPrimary)
                    .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = BgPrimary
    ) { padding ->
        if (dayStamps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No specimens found for this day.", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dayStamps) { stamp ->
                    Surface(
                        modifier = Modifier
                            .aspectRatio(0.8f)
                            .AtmosphericShadow()
                            .clickable { onStampClick(stamp.id) },
                        shape = RoundedCornerShape(24.dp),
                        color = SurfaceCard
                    ) {
                        val file = File(stamp.imagePath)
                        if (file.exists()) {
                            Image(
                                painter = rememberAsyncImagePainter(Uri.fromFile(file)),
                                contentDescription = stamp.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(12.dp).background(TertiaryFixed)
                            )
                        }
                    }
                }
            }
        }
    }
}
