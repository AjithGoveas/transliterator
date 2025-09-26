package dev.ajithgoveas.transliterator.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

@Composable
fun CameraPreview(
    onImageCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = ProcessCameraProvider.getInstance(ctx).get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Shutter button styled like a real camera app
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .shadow(6.dp, CircleShape)
                .background(Color.White, CircleShape)
                .clickable {
                    val output = ImageCapture.OutputFileOptions.Builder(
                        File.createTempFile("ocr", ".jpg")
                    ).build()

                    imageCapture.takePicture(
                        output,
                        context.mainExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val bitmap =
                                    BitmapFactory.decodeFile(outputFileResults.savedUri?.path)
                                onImageCaptured(bitmap)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                // Handle error gracefully
                            }
                        }
                    )
                }
        )
    }
}