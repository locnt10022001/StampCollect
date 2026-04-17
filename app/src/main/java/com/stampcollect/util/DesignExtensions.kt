package com.stampcollect.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.stampcollect.ui.theme.GlassWhite
import com.stampcollect.ui.theme.Primary
import com.stampcollect.ui.theme.PrimaryContainer

/**
 * Implements the "Atmospheric Shadow" principle from the Curated Heritage design system.
 * 6% opacity, 0px X-offset, 12px Y-offset, 48px Blur.
 */
fun Modifier.AtmosphericShadow() = this.then(
    Modifier.shadow(
        elevation = 12.dp,
        shape = RoundedCornerShape(24.dp),
        clip = false,
        ambientColor = Color.Black.copy(alpha = 0.06f),
        spotColor = Color.Black.copy(alpha = 0.06f)
    )
)

/**
 * Applies the "Silk" linear gradient for buttons and active headers.
 */
fun Modifier.SilkGradient() = this.then(
    Modifier.background(
        Brush.linearGradient(
            colors = listOf(Primary, PrimaryContainer),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    )
)

/**
 * Signature Glassmorphism: High-end semi-transparent finish.
 * Note: We avoid .blur() on the modifier as it blurs the children (text/icons).
 */
fun Modifier.GlassMorphic(radius: Int = 0) = this.then(
    Modifier
        .clip(RoundedCornerShape(radius.dp))
        .background(GlassWhite.copy(alpha = 0.85f))
)

/**
 * Subtle entrance animation (fade + slide-up).
 */
@Composable
fun Modifier.EntranceAnimation(delay: Int = 0): Modifier {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        animated = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "Alpha"
    )
    
    val slideY by animateFloatAsState(
        targetValue = if (animated) 0f else 30f,
        animationSpec = tween(durationMillis = 600),
        label = "SlideY"
    )

    return this.graphicsLayer {
        this.alpha = alpha
        this.translationY = slideY
    }
}
