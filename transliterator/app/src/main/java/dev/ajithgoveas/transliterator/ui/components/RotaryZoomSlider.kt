package dev.ajithgoveas.transliterator.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ajithgoveas.transliterator.utils.generateFriendlyAnchors
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun RotaryZoomSlider(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 320.dp,
    minZoom: Float = 1f,
    maxZoom: Float = 6f,
    initialZoom: Float = 1f,
    onZoomChange: (Float) -> Unit = {}
) {
    val density = LocalDensity.current

    val minLog = ln(minZoom)
    val maxLog = ln(maxZoom)

    var offset by remember { mutableFloatStateOf(0f) }

    // Sync initial zoom with wheel
    LaunchedEffect(initialZoom) {
        offset = ((ln(initialZoom) - minLog) / (maxLog - minLog)).coerceIn(0f, 1f)
    }

    val currentZoom by remember {
        derivedStateOf { exp(minLog + offset * (maxLog - minLog)) }
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var dragStartAngle by remember { mutableFloatStateOf(0f) }
    var dragStartOffset by remember { mutableFloatStateOf(0f) }

    fun angleFromTop(dx: Float, dy: Float): Float =
        Math.toDegrees(atan2(dx.toDouble(), (-dy).toDouble())).toFloat()

    Box(
        modifier = modifier
            .width(sizeDp)
            .height(sizeDp / 2),
        contentAlignment = Alignment.Center
    ) {
        val pointerModifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { down ->
                    val cx = canvasSize.width / 2f
                    val cy = canvasSize.height
                    dragStartAngle = angleFromTop(down.x - cx, down.y - cy)
                    dragStartOffset = offset
                },
                onDrag = { change, _ ->
                    change.consume()
                    val cx = canvasSize.width / 2f
                    val cy = canvasSize.height
                    val currentAngle = angleFromTop(change.position.x - cx, change.position.y - cy)
                    val deltaAngle = when {
                        (currentAngle - dragStartAngle) > 180f -> currentAngle - dragStartAngle - 360f
                        (currentAngle - dragStartAngle) < -180f -> currentAngle - dragStartAngle + 360f
                        else -> currentAngle - dragStartAngle
                    }
                    offset = (dragStartOffset + deltaAngle / 180f).coerceIn(0f, 1f)
                    onZoomChange(currentZoom)
                }
            )
        }

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .then(pointerModifier)
        ) {
            canvasSize = size

            val cx = size.width / 2f
            val cy = size.height
            val radius = size.width / 2f

            // --- Semicircle background ---
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF111112), Color.Black),
                    center = Offset(cx, cy),
                    radius = radius
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2)
            )

            // --- Rim ---
            drawArc(
                color = Color.White.copy(alpha = 0.1f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(1.5f)
            )

            // --- Ticks ---
            val totalTicks = 120
            val majorEvery = 10
            repeat(totalTicks + 1) { i ->
                val t = i / totalTicks.toFloat()
                val relative = t - offset
                val angle = relative * 180f
                if (angle in -90f..90f) {
                    val rad = Math.toRadians(angle.toDouble())
                    val outer = Offset(
                        cx + radius * sin(rad).toFloat(),
                        cy - radius * cos(rad).toFloat()
                    )
                    val isMajor = i % majorEvery == 0
                    val tickLen = with(density) { (if (isMajor) 16.dp else 8.dp).toPx() }
                    val alpha = 0.25f + 0.55f * (1f - abs(angle) / 90f)

                    val inner = Offset(
                        cx + (radius - tickLen) * sin(rad).toFloat(),
                        cy - (radius - tickLen) * cos(rad).toFloat()
                    )

                    drawLine(
                        color = Color.White.copy(alpha = alpha),
                        start = inner,
                        end = outer,
                        strokeWidth = if (isMajor) 2f else 1f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // --- Dynamic anchors (6 steps, log spaced) ---
            val anchors = generateFriendlyAnchors(minZoom, maxZoom)
            val labelPadding = with(density) { 20.dp.toPx() }
            val labelRadius = radius - labelPadding

            // Bubble lens
            val bubbleW = with(density) { 76.dp.toPx() }
            val bubbleH = with(density) { 34.dp.toPx() }
            val bubbleCenter = Offset(
                cx,
                cy - labelRadius - bubbleH / 2f + with(density) { 18.dp.toPx() }
            )
            val bubbleRect = Rect(
                bubbleCenter.x - bubbleW / 2f,
                bubbleCenter.y - bubbleH / 2f,
                bubbleCenter.x + bubbleW / 2f,
                bubbleCenter.y + bubbleH / 2f
            )

            // --- Labels ---
            anchors.forEach { zoom ->
                val t = (ln(zoom) - minLog) / (maxLog - minLog)
                val relative = t - offset
                val angle = relative * 180f
                if (angle in -90f..90f) {
                    val rad = Math.toRadians(angle.toDouble())
                    val x = cx + labelRadius * sin(rad).toFloat()
                    val y = cy - labelRadius * cos(rad).toFloat()

                    val insideBubble = bubbleRect.contains(Offset(x, y))
                    val eased = sqrt((1f - abs(angle) / 90f).coerceIn(0f, 1f))
                    val alpha = if (insideBubble) 1f else (0.3f + 0.7f * eased)
                    val scale = if (insideBubble) 1.3f else (0.9f + 0.5f * eased)

                    val labelText = if (zoom < 1.0f) {
                        String.format("%.1fx", zoom) // show 0.5x, 0.8x
                    } else {
                        if (zoom % 1f == 0f) "${zoom.toInt()}x"
                        else String.format("%.1fx", zoom)
                    }

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = Color.White.copy(alpha = alpha).toArgb()
                            textSize = with(density) { (12f * scale).sp.toPx() }
                            isAntiAlias = true
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = insideBubble || eased > 0.95f
                        }
                        drawText(labelText, x, y + paint.textSize / 3f, paint)
                    }
                }
            }

            // --- Bubble Overlay ---
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(bubbleRect.left, bubbleRect.top + with(density) { 4.dp.toPx() }),
                size = Size(bubbleW, bubbleH),
                cornerRadius = CornerRadius(bubbleH / 2f, bubbleH / 2f)
            )
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))
                ),
                topLeft = Offset(bubbleRect.left, bubbleRect.top),
                size = Size(bubbleW, bubbleH),
                cornerRadius = CornerRadius(bubbleH / 2f, bubbleH / 2f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.16f))
                ),
                topLeft = Offset(bubbleRect.left, bubbleRect.top),
                size = Size(bubbleW, bubbleH),
                cornerRadius = CornerRadius(bubbleH / 2f, bubbleH / 2f),
                style = Stroke(1.2f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SemiCircularZoomWheelPreview() {
    RotaryZoomSlider()
}