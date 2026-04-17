package com.stampcollect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.stampcollect.R

// --- Google Fonts Setup ---

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Headlines: Newsreader (High-contrast Serif)
val NewsreaderFont = FontFamily(
    Font(googleFont = GoogleFont("Newsreader"), fontProvider = provider)
)

// Data & UI Elements: Manrope (Geometric Sans-serif)
val ManropeFont = FontFamily(
    Font(googleFont = GoogleFont("Manrope"), fontProvider = provider)
)

// --- Typography Definition ---

val StampTypography = Typography(
    // Hero & Large Titles
    displayLarge = TextStyle(
        fontFamily = NewsreaderFont,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = (-0.5).sp,
        lineHeight = 48.sp
    ),
    displayMedium = TextStyle(
        fontFamily = NewsreaderFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp,
        lineHeight = 38.sp
    ),
    
    // Section Headings
    headlineLarge = TextStyle(
        fontFamily = NewsreaderFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NewsreaderFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    
    // Titles & Functional UI
    titleLarge = TextStyle(
        fontFamily = ManropeFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp // Captioned gallery feel
    ),
    
    // Body Text
    bodyLarge = TextStyle(
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    
    // Metadata & Labels
    labelLarge = TextStyle(
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ManropeFont,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.8.sp, // All-caps caption feel
        lineHeight = 14.sp
    )
)
