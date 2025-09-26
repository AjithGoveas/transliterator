package dev.ajithgoveas.transliterator.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

@Composable
fun CameraPreview(
    onImageCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProvider = ProcessCameraProvider.Companion.getInstance(ctx).get()
            val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
            val imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.bindToLifecycle(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector,
                preview,
                imageCapture
            )

            previewView
        },
        update = { previewView ->
            // No-op
        }
    )

    Button(
        onClick = {
            val imageCapture = ImageCapture.Builder().build()
            val output = ImageCapture.OutputFileOptions.Builder(File.createTempFile("ocr", ".jpg")).build()
            imageCapture.takePicture(
                output,
                context.mainExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val bitmap = BitmapFactory.decodeFile(outputFileResults.savedUri?.path)
                        onImageCaptured(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        TODO("Not yet implemented")
                    }
                }
            )
        }
    ) {
        Text("Shutter")
    }
}