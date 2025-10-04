package dev.ajithgoveas.transliterator.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.ui.geometry.Rect
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ajithgoveas.transliterator.ocr.MLKitTextRecognition
import dev.ajithgoveas.transliterator.translit.AksharamukhaClient
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class TranslitViewModel @Inject constructor(
    private val mlKitTextRecognition: MLKitTextRecognition,
    private val transliterator: AksharamukhaClient
) : ViewModel() {

    // --- UI State ---
    private val _ocrText = MutableStateFlow("")
    val ocrText: StateFlow<String> = _ocrText

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result

    private val _showSheet = MutableStateFlow(false)
    val showSheet: StateFlow<Boolean> = _showSheet

    private val _cropRect = MutableStateFlow(Rect(100f, 100f, 600f, 600f))
    val cropRect: StateFlow<Rect> = _cropRect

    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled

    // --- CameraX references ---
    private var imageCapture: ImageCapture? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    // --- Zoom state ---
    private val _zoomRatio = MutableStateFlow(1f)
    val zoomRatio: StateFlow<Float> = _zoomRatio

    private val _minZoom = MutableStateFlow(1f)
    val minZoom: StateFlow<Float> = _minZoom

    private val _maxZoom = MutableStateFlow(1f)
    val maxZoom: StateFlow<Float> = _maxZoom

    // --- Camera setup ---
    fun setImageCapture(capture: ImageCapture) {
        imageCapture = capture
        updateFlashMode()
    }

    fun setCameraControl(control: CameraControl, info: CameraInfo, lifecycleOwner: LifecycleOwner) {
        cameraControl = control
        cameraInfo = info

        // Lifecycle-safe zoom state observation
        info.zoomState.observe(lifecycleOwner) { zoomState ->
            _zoomRatio.value = zoomState.zoomRatio
            _minZoom.value = zoomState.minZoomRatio
            _maxZoom.value = zoomState.maxZoomRatio
        }
    }

    // --- Image capture ---
    fun captureImage(context: Context) {
        val capture = imageCapture ?: return
        val file = File.createTempFile("ocr", ".jpg", context.cacheDir)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    processImage(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }

    // --- OCR + Transliteration ---
    private fun processImage(bitmap: Bitmap) {
        val rect = _cropRect.value

        val safeWidth = rect.width.toInt().coerceAtMost(bitmap.width - rect.left.toInt())
        val safeHeight = rect.height.toInt().coerceAtMost(bitmap.height - rect.top.toInt())

        val cropped = Bitmap.createBitmap(
            bitmap,
            rect.left.toInt().coerceAtLeast(0),
            rect.top.toInt().coerceAtLeast(0),
            safeWidth.coerceAtLeast(1),
            safeHeight.coerceAtLeast(1)
        )

        // OCR + Transliteration in background
        // --- Old ML Kit code, kept for reference ---
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        viewModelScope.launch(Dispatchers.IO) {
            mlKitTextRecognition.recognizeText(inputImage) { text ->
                _ocrText.value = text

                transliterator.transliterate("Devanagari", "Kannada", text) { translitText ->
                    _result.value = translitText
                    _showSheet.value = true
                }
            }
        }
        /*
        // --- old tess code, kept for reference ---
        viewModelScope.launch(Dispatchers.IO) {
            val text = performOcrUseCase(bitmap = bitmap)  // ✅ use cropped
            _ocrText.value = text

            transliterator.transliterate("Devanagari", "Kannada", text) { translitText ->
                _result.value = translitText
                _showSheet.value = true
            }
        }
         */

        /*
        // --- New TFLite OCR + transliteration code ---
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val language = Language.ENGLISH
                val result = recognizeTextUseCase(bitmap, language)
                _ocrText.value = result.lines.joinToString("\n") { it.text }
            } catch (e: Exception) {
                _ocrText.value = "Error: ${e.message}"
            } finally {
                _isProcessing.value = false
                _showSheet.value = true
            }
        }
         */
    }

    // --- Bottom sheet control ---
    fun hideSheet() {
        _showSheet.value = false
    }

    // --- Crop box control ---
    fun updateCropRect(rect: Rect) {
        _cropRect.value = rect
    }

    fun resetCropRect() {
        _cropRect.value = Rect(100f, 100f, 600f, 600f)
    }

    // --- Zoom controls ---
    fun setZoom(zoom: Float) {
        val clampedZoom = zoom.coerceIn(_minZoom.value, _maxZoom.value)
        cameraControl?.setZoomRatio(clampedZoom)
        _zoomRatio.value = clampedZoom
    }

    fun zoomIn(step: Float = 0.1f) = setZoom(_zoomRatio.value + step)
    fun zoomOut(step: Float = 0.1f) = setZoom(_zoomRatio.value - step)

    // --- Flash control ---
    fun toggleFlash() {
        _flashEnabled.value = !_flashEnabled.value
        updateFlashMode()
    }

    private fun updateFlashMode() {
        imageCapture?.flashMode =
            if (_flashEnabled.value) ImageCapture.FLASH_MODE_ON
            else ImageCapture.FLASH_MODE_OFF
    }
}