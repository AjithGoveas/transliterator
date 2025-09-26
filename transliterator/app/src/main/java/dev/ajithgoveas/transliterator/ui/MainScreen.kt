package dev.ajithgoveas.transliterator.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.ajithgoveas.transliterator.viewmodel.TranslitViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: TranslitViewModel
) {
    Box(
        modifier = modifier
    ) {
        CameraPreview { bitmap ->
            viewModel.processImage(bitmap)
        }
        if (viewModel.showSheet) {
            TranslitBottomSheet(ocr_text = viewModel.ocr_text, result = viewModel.result, expanded = true) {
                viewModel.showSheet = false
            }
        }
    }
}