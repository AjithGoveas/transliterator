package dev.ajithgoveas.transliterator.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslitBottomSheet(
    modifier: Modifier = Modifier,
    ocr_text: String,
    result: String,
    expanded: Boolean,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(expanded))
    {
        Text("Original Text", style = MaterialTheme.typography.titleLarge)
        Text(ocr_text, modifier = Modifier.padding(16.dp))
        Text("Transliterated Text", style = MaterialTheme.typography.titleLarge)
        Text(result, modifier = Modifier.padding(16.dp))
    }
}