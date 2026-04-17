package com.stampcollect.ui.components.camera

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

/**
 * Modern stamp frame with rounded corners and clean borders.
 */
fun DrawScope.drawModernFrame(left: Float, top: Float, width: Float, height: Float) {
    val borderX = width * 0.06f
    val borderY = height * 0.12f
    val totalW = width + borderX * 2
    val totalH = height + borderY * 2
    val borderL = left - borderX
    val borderT = top - borderY

    drawRoundRect(
        color = Color.White,
        topLeft = Offset(borderL, borderT),
        size = Size(totalW, totalH),
        cornerRadius = CornerRadius(24.dp.toPx())
    )
    drawRect(
        color = Color.Transparent,
        topLeft = Offset(left, top),
        size = Size(width, height),
        blendMode = BlendMode.Clear
    )
}

/**
 * Classic stamp frame with realistic perforations.
 */
fun DrawScope.drawClassicFrame(left: Float, top: Float, width: Float, height: Float, perf: Float) {
    val borderThick = perf * 1.6f
    val perfStep = perf * 2.5f
    val borderL = left - borderThick
    val borderT = top - borderThick
    val borderW = width + borderThick * 2
    val borderH = height + borderThick * 2

    // Parchment Surface
    drawRect(color = Color(0xFFE4E2DD), topLeft = Offset(borderL, borderT), size = Size(borderW, borderH))

    // Perforations (Clear Holes)
    val midTop = borderT + borderThick / 2f
    var x = borderL + perfStep / 2f
    while (x <= borderL + borderW) {
        drawCircle(color = Color.Transparent, radius = perf, center = Offset(x, midTop), blendMode = BlendMode.Clear)
        drawCircle(color = Color.Transparent, radius = perf, center = Offset(x, borderT + borderH - borderThick / 2f), blendMode = BlendMode.Clear)
        x += perfStep
    }
    var y = borderT + perfStep / 2f
    while (y <= borderT + borderH) {
        drawCircle(color = Color.Transparent, radius = perf, center = Offset(borderL + borderThick / 2f, y), blendMode = BlendMode.Clear)
        drawCircle(color = Color.Transparent, radius = perf, center = Offset(borderL + borderW - borderThick / 2f, y), blendMode = BlendMode.Clear)
        y += perfStep
    }

    drawRect(color = Color.Transparent, topLeft = Offset(left, top), size = Size(width, height), blendMode = BlendMode.Clear)
}

/**
 * Scalloped vintage frame with rounded decorative edges.
 */
fun DrawScope.drawScallopedFrame(left: Float, top: Float, width: Float, height: Float) {
    val borderThick = width * 0.08f
    val perfR = borderThick * 0.6f
    val perfStep = perfR * 2.2f
    val borderL = left - borderThick
    val borderT = top - borderThick
    val borderW = width + borderThick * 2
    val borderH = height + borderThick * 2
    
    val bgPaint = Color(0xFFE4E2DD)
    drawRect(color = bgPaint, topLeft = Offset(borderL + perfR, borderT + perfR), size = Size(borderW - perfR*2, borderH - perfR*2))
    
    var x = borderL + perfStep/2f
    while (x <= borderL + borderW) {
        drawCircle(color = bgPaint, radius = perfR, center = Offset(x, borderT + perfR/2f))
        drawCircle(color = bgPaint, radius = perfR, center = Offset(x, borderT + borderH - perfR/2f))
        x += perfStep
    }
    var y = borderT + perfStep/2f
    while (y <= borderT + borderH) {
        drawCircle(color = bgPaint, radius = perfR, center = Offset(borderL + perfR/2f, y))
        drawCircle(color = bgPaint, radius = perfR, center = Offset(borderL + borderW - perfR/2f, y))
        y += perfStep
    }
    drawRect(color = Color.Transparent, topLeft = Offset(left, top), size = Size(width, height), blendMode = BlendMode.Clear)
}
