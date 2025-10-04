package dev.ajithgoveas.transliterator.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
    ocrText: String,
    result: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState)
    {
        LazyColumn {
            item {
                Text("Original Text", style = MaterialTheme.typography.titleLarge)
                Text(ocrText, modifier = Modifier.padding(16.dp))
                Text("Transliterated Text", style = MaterialTheme.typography.titleLarge)
                Text(result, modifier = Modifier.padding(16.dp))
            }
        }
    }
}