package dev.ajithgoveas.transliterator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopControls(
    isFlashOn: Boolean,
    onFlashToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        /*
        IconButton(onClick = { /* Close */ }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
         */

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ControlButton(
                icon = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                onClick = onFlashToggle
            )
            /*
            ControlButton(
                icon = Icons.Default.AspectRatio,
                onClick = { /* Aspect ratio */ }
            )
            ControlButton(
                icon = Icons.Default.Timer,
                onClick = { /* Timer */ }
            )
            ControlButton(
                icon = Icons.Default.FilterVintage,
                onClick = { /* Filters */ }
            )
             */
        }
    }
}