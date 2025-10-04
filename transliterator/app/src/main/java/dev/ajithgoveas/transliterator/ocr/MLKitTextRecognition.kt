package dev.ajithgoveas.transliterator.ocr

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import jakarta.inject.Inject

class MLKitTextRecognition @Inject constructor() {

    private val recognizer =
        TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

    /**
     * Recognizes text from a pre-constructed InputImage.
     * @param image ML Kit InputImage, already containing bitmap/rotation info
     * @param onResult Callback with recognized text or error message
     */
    fun recognizeText(image: InputImage, onResult: (String) -> Unit) {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                if (text.isNotBlank()) {
                    onResult(text)
                } else {
                    onResult("No text detected")
                }
            }
            .addOnFailureListener { e ->
                onResult("Error: ${e.message}")
            }
    }
}
