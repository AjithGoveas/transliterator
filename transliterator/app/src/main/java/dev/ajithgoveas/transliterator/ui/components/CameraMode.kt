package dev.ajithgoveas.transliterator.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CameraModes(
    selectedMode: String,
    onModeSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf("TIME-LAPSE", "SLO-MO", "CINEMATIC", "VIDEO", "PHOTO", "PORTRAIT", "PANO")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        modes.forEach { mode ->
            val isSelected = mode == selectedMode

            Text(
                text = mode,
                fontSize = if (isSelected) 16.sp else 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures { onModeSelect(mode) }
                }
            )
        }
    }
}