package dev.ajithgoveas.transliterator.ocr

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions

class MLKitTextRecognition {
    private val recognizer =
        TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

    fun recognizeText(image: InputImage, onResult: (String) -> Unit) {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                onResult(visionText.text)
            }
            .addOnFailureListener { e ->
                onResult("Error: ${e.message}")
            }
    }
}