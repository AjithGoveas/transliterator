package dev.ajithgoveas.transliterator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ajithgoveas.transliterator.utils.generateFriendlyAnchors
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt

@Composable
fun IPhoneZoomButtons(
    minZoom: Float,
    maxZoom: Float,
    currentZoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    // 6 evenly spaced zoom stops (logarithmic scale)
    val anchors = generateFriendlyAnchors(minZoom, maxZoom)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        anchors.forEach { zoom ->
            val isSelected = abs(currentZoom - zoom) < 0.15f // tolerance
            val label = zoom.toZoomLabel()

            Box(
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) Color(0xFFFFD60A)
                        else Color.White.copy(alpha = 0.2f)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { onZoomChange(zoom) }
                    }
                    .semantics { contentDescription = "Zoom $label" }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.Black else Color.White
                )
            }
        }
    }
}

/**
 * Converts zoom float into a readable string like "0.5×", "1×", "2×".
 */
private fun Float.toZoomLabel(): String {
    return if (this < 1f) {
        // round to one decimal for sub-1x zoom
        String.format("%.1f×", this)
    } else {
        // avoid "2.0×", show "2×"
        "${this.roundToInt()}×"
    }
}

// --- Helpers for external mapping ---
fun zoomToPosition(zoom: Float, minZoom: Float, maxZoom: Float): Float =
    ((zoom - minZoom) / (maxZoom - minZoom)).coerceIn(0f, 1f)

fun positionToZoom(position: Float, minZoom: Float, maxZoom: Float): Float =
    (minZoom + position * (maxZoom - minZoom)).coerceIn(minZoom, maxZoom)