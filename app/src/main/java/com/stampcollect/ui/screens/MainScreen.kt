package com.stampcollect.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stampcollect.ui.theme.*
import com.stampcollect.util.AtmosphericShadow
import com.stampcollect.ui.viewmodel.CollectionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    onCollectionClick: (Int, String) -> Unit,
    onDayClick: (Long) -> Unit,
    onStampClick: (Int) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(1) }

    data class Tab(val selected: ImageVector, val unselected: ImageVector)
    val tabs = listOf(
        Tab(Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
        Tab(Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
        Tab(Icons.Filled.Collections, Icons.Outlined.Collections)
    )

    Box(modifier = Modifier.fillMaxSize().background(BgPrimary)) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> CalendarScreen(onDayClick = onDayClick, viewModel = viewModel)
                1 -> CameraScreen(viewModel = viewModel)
                2 -> HomeScreen(
                    onCollectionClick = onCollectionClick,
                    onStampClick = onStampClick,
                    viewModel = viewModel
                )
            }
        }

        // Minimal floating icon-only pill bar (The "Archives Navigator")
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(GlassWhite.copy(alpha = 0.9f)) // Slightly higher opacity for visibility
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .AtmosphericShadow(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                val tint by animateColorAsState(
                    targetValue = if (isSelected) Primary else TextSecondary,
                    animationSpec = tween(200)
                )

                IconButton(
                    onClick = { selectedTab = index },
                    modifier = Modifier
                        .size(48.dp)
                        .then(
                            if (isSelected) Modifier
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.08f))
                            else Modifier
                        )
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.selected else tab.unselected,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
