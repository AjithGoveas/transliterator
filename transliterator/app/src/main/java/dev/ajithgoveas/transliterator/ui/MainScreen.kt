package dev.ajithgoveas.transliterator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.ajithgoveas.transliterator.ui.components.*
import dev.ajithgoveas.transliterator.viewmodel.TranslitViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: TranslitViewModel
) {
    val context = LocalContext.current

    // --- Collect state from ViewModel ---
    val showSheet by viewModel.showSheet.collectAsState()
    val result by viewModel.result.collectAsState()
    val ocrText by viewModel.ocrText.collectAsState()
    val isFlashOn by viewModel.flashEnabled.collectAsState()
    val currentZoom by viewModel.zoomRatio.collectAsState()
    val minZoom by viewModel.minZoom.collectAsState()
    val maxZoom by viewModel.maxZoom.collectAsState()

    var showZoomWheel by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Feed
        CameraPreview(viewModel = viewModel)

        // Top controls (Flash toggle etc.)
        TopControls(
            isFlashOn = isFlashOn,
            onFlashToggle = { viewModel.toggleFlash() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // Zoom Controls (Buttons + Rotary Wheel)
        ZoomControls(
            minZoom = minZoom,
            maxZoom = maxZoom,
            currentZoom = currentZoom,
            showZoomWheel = showZoomWheel,
            onZoomChange = { viewModel.setZoom(it) },
            onWheelVisibilityChanged = { showZoomWheel = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)   // <-- alignment here
        )


        // Capture Controls
        CaptureControls(
            onCapture = { viewModel.captureImage(context) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 32.dp, end = 32.dp)
        )

        // Bottom Sheet for OCR + Transliteration results
        if (showSheet) {
            TranslitBottomSheet(
                ocrText = ocrText,
                result = result,
                onDismiss = { viewModel.hideSheet() }
            )
        }
    }
}

@Composable
private fun ZoomControls(
    minZoom: Float,
    maxZoom: Float,
    currentZoom: Float,
    showZoomWheel: Boolean,
    onZoomChange: (Float) -> Unit,
    onWheelVisibilityChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onWheelVisibilityChanged(true) },
                    onDragEnd = { onWheelVisibilityChanged(false) },
                    onDragCancel = { onWheelVisibilityChanged(false) },
                    onDrag = { change, _ ->
                        val boxHeight = size.height.toFloat()
                        val y = change.position.y.coerceIn(0f, boxHeight)
                        val position = y / boxHeight
                        val zoom = (minZoom + (1 - position) * (maxZoom - minZoom))
                            .coerceIn(minZoom, maxZoom)
                        onZoomChange(zoom)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = !showZoomWheel) {
            IPhoneZoomButtons(
                minZoom = minZoom,
                maxZoom = maxZoom,
                currentZoom = currentZoom,
                onZoomChange = onZoomChange,
                modifier = Modifier.padding(bottom = 132.dp)
            )
        }

        AnimatedVisibility(visible = showZoomWheel) {
            RotaryZoomSlider(
                minZoom = minZoom,
                maxZoom = maxZoom,
                initialZoom = currentZoom,
                onZoomChange = onZoomChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}