package dev.ajithgoveas.transliterator.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import dev.ajithgoveas.transliterator.ocr.MLKitTextRecognition
import dev.ajithgoveas.transliterator.translit.AksharamukhaClient

class TranslitViewModel : ViewModel() {
    var ocr_text by mutableStateOf("")
    var result by mutableStateOf("")
    var showSheet by mutableStateOf(false)

    fun processImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        MLKitTextRecognition().recognizeText(image = image) { text ->
            ocr_text = text
            AksharamukhaClient.transliterate(
                source = "Devanagari",
                target = "Kannada",
                text = text
            ) { transliteratedText ->
                result = transliteratedText
                showSheet = true
            }
        }
    }
}