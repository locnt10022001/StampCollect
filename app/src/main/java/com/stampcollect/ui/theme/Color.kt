package com.stampcollect.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// --- Curated Heritage Palette ---

// Primary - Authority & Depth (Royal Blue)
val Primary = Color(0xFF003178)        
val PrimaryContainer = Color(0xFF0D47A1)
val OnPrimary = Color(0xFFFFFFFF)

// Secondary - Emotional energy (Coral)
val Secondary = Color(0xFFB51925)
val OnSecondary = Color(0xFFFFFFFF)

// Surfaces - Architectural separation (No-Line Philosophy)
val BgPrimary = Color(0xFFF9F9F9)      // "Gallery Floor" - Base Layer
val SurfaceSection = Color(0xFFF3F3F3) // "Section Layer" - Content Groupings
val SurfaceCard = Color(0xFFFFFFFF)    // "Interactive Layer" - Cards
val SurfaceOverlay = Color(0xE60D0D0D) // 90% black for overlays

// Tertiary - Specimen Mount (Parchment)
val TertiaryFixed = Color(0xFFE4E2DD)
val StampPaper = Color(0xFFE4E2DD)     // Parchment tone for artifact backgrounds

// Typography - Autoritative Contrast
val TextPrimary = Color(0xFF1A1A1A)    // Near black
val TextSecondary = Color(0xFF555555)  // Mid gray
val TextTertiary = Color(0xFF9E9E9E)
val TextOnDark = Color(0xFFF9F9F9)

// Helper Colors
val Coral = Color(0xFFB51925)
val Surface200 = Color(0xFFE0E0E0) // Ghost border fallback (at 15% opacity)
val OutlineVariant = Color(0xFFE0E0E0).copy(alpha = 0.15f)

// Signature "Silk" Gradient
val SilkGradient = Brush.linearGradient(
    colors = listOf(Primary, PrimaryContainer),
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

// Legacy Compatibility (Mapping old names to new palette where applicable)
val BgSecondary = SurfaceSection
val BgCard = SurfaceCard
val Accent = PrimaryContainer
val AccentLight = Primary
val SkyBlue = Primary
val Mint = Color(0xFF2E7D32) // Keep current green for now or update later
val GlassWhite = Color(0xCCFFFFFF) // 80% opacity for Glassmorphism
val GlassDark = Color(0x99000000)
val PillBg = Color(0xF5FFFFFF)
val BgOverlay = SurfaceOverlay

// --- Thematic Collection Backdrops (Restored for CollectionDetailScreen) ---
val StampPaperGrid  = Color(0xFFF4F0E6)   // Slightly darker for grid
val StampPaperCork  = Color(0xFFD7CCC8)   // Warm cork-like brown
val StampPaperVelvet= Color(0xFF1A237E)   // Deep navy velvet
val StampPaperLinen = Color(0xFFECEFF1)   // Cool linen gray
