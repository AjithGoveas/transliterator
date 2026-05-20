package dev.ajithgoveas.transliterator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.ajithgoveas.transliterator.viewmodel.TranslitViewModel
import kotlin.math.roundToInt

@Composable
fun CropOverlay(viewModel: TranslitViewModel) {
    var offsetX by remember { mutableFloatStateOf(100f) }
    var offsetY by remember { mutableFloatStateOf(400f) }
    var boxWidth by remember { mutableFloatStateOf(200f) }
    var boxHeight by remember { mutableFloatStateOf(200f) }

    val handleSize = 24.dp
    val minSize = 100f
    var activeHandle by remember { mutableStateOf<Handle?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { pos ->
                        activeHandle = getHandleTouched(
                            pos.x, pos.y, offsetX, offsetY, boxWidth, boxHeight, handleSize.toPx()
                        )
                    },
                    onDragEnd = { activeHandle = null }
                ) { change, drag ->
                    when (activeHandle) {
                        Handle.MOVE -> {
                            offsetX = (offsetX + drag.x).coerceIn(0f, size.width - boxWidth)
                            offsetY = (offsetY + drag.y).coerceIn(0f, size.height - boxHeight)
                        }

                        Handle.TOP_LEFT -> {
                            val newX = (offsetX + drag.x).coerceIn(0f, offsetX + boxWidth - minSize)
                            val newY = (offsetY + drag.y).coerceIn(0f, offsetY + boxHeight - minSize)
                            boxWidth += offsetX - newX
                            boxHeight += offsetY - newY
                            offsetX = newX
                            offsetY = newY
                        }

                        Handle.TOP_RIGHT -> {
                            val newW = (boxWidth + drag.x).coerceAtLeast(minSize)
                                .coerceAtMost(size.width - offsetX)
                            val newY = (offsetY + drag.y).coerceIn(0f, offsetY + boxHeight - minSize)
                            boxHeight += offsetY - newY
                            offsetY = newY
                            boxWidth = newW
                        }

                        Handle.BOTTOM_LEFT -> {
                            val newX = (offsetX + drag.x).coerceIn(0f, offsetX + boxWidth - minSize)
                            val newH = (boxHeight + drag.y).coerceAtLeast(minSize)
                                .coerceAtMost(size.height - offsetY)
                            boxWidth += offsetX - newX
                            offsetX = newX
                            boxHeight = newH
                        }

                        Handle.BOTTOM_RIGHT -> {
                            boxWidth = (boxWidth + drag.x).coerceAtLeast(minSize)
                                .coerceAtMost(size.width - offsetX)
                            boxHeight = (boxHeight + drag.y).coerceAtLeast(minSize)
                                .coerceAtMost(size.height - offsetY)
                        }

                        null -> Unit
                    }

                    // Send updated crop rect to ViewModel
                    viewModel.updateCropRect(Rect(offsetX, offsetY, offsetX + boxWidth, offsetY + boxHeight))
                }
            }
    ) {
        // Dimmed background outside crop area
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // Transparent "hole" for the crop region
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(boxWidth.dp, boxHeight.dp)
                .border(2.dp, Color.White)
                .background(Color.Transparent)
        ) {
            // Four corner handles
            HandleCircle(Modifier.align(Alignment.TopStart), handleSize)
            HandleCircle(Modifier.align(Alignment.TopEnd), handleSize)
            HandleCircle(Modifier.align(Alignment.BottomStart), handleSize)
            HandleCircle(Modifier.align(Alignment.BottomEnd), handleSize)
        }
    }
}

@Composable
private fun HandleCircle(modifier: Modifier, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color.White, CircleShape)
            .border(2.dp, Color.Blue, CircleShape)
    )
}

private enum class Handle { MOVE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

/**
 * Detects which handle was touched, otherwise MOVE.
 */
private fun getHandleTouched(
    x: Float,
    y: Float,
    offsetX: Float,
    offsetY: Float,
    width: Float,
    height: Float,
    handleSize: Float
): Handle {
    val left = offsetX
    val top = offsetY
    val right = offsetX + width
    val bottom = offsetY + height

    return when {
        (x in (left - handleSize)..(left + handleSize) &&
                y in (top - handleSize)..(top + handleSize)) -> Handle.TOP_LEFT

        (x in (right - handleSize)..(right + handleSize) &&
                y in (top - handleSize)..(top + handleSize)) -> Handle.TOP_RIGHT

        (x in (left - handleSize)..(left + handleSize) &&
                y in (bottom - handleSize)..(bottom + handleSize)) -> Handle.BOTTOM_LEFT

        (x in (right - handleSize)..(right + handleSize) &&
                y in (bottom - handleSize)..(bottom + handleSize)) -> Handle.BOTTOM_RIGHT

        (x in left..right && y in top..bottom) -> Handle.MOVE

        else -> Handle.MOVE
    }
}
