# Transliterator Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-minSdk%2028-brightgreen.svg?style=flat-square&logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Compose-yes-orange.svg?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Dagger--Hilt-Inject-purple.svg?style=flat-square&logo=dagger)](https://developer.android.com/training/dependency-injection/hilt-android)

A native Android mobile application built using **Kotlin**, **Jetpack Compose**, **CameraX**, and **Google ML Kit**. It provides real-time optical character recognition (OCR) targeting Devanagari script from camera input and performs dynamic script transliteration by querying a backend service.

---

## ✨ Key Features & Modern UI Polish

*   **Live Camera View:** Built using modern CameraX Jetpack Compose interop to display high-framerate camera surfaces (`CameraPreview.kt`).
*   **Draggable Crop Overlay:** An interactive crop UI helper with 4 custom-drawn corner handle circles. Users can drag the handles to resize the crop target region, or drag inside the box to reposition the crop target (`CropOverlay.kt`). Only the cropped bitmap area is sent to the OCR model for enhanced performance and reduced background noise.
*   **Advanced Focal Control (Zoom):**
    *   *iOS-Style Preset Zoom Buttons:* Quick tap zoom thresholds (e.g. 1x, 2x, etc.) using `IPhoneZoomButtons` (`ZoomButtons.kt`).
    *   *Draggable Rotary Slider:* A scroll-dial rotary wheel (`RotaryZoomSlider.kt`) that appears when dragging to allow smooth, precise camera focus adjustments.
*   **Devanagari OCR:** Utilizes Google ML Kit Text Recognition with `DevanagariTextRecognizerOptions` to read Hindi, Sanskrit, Marathi, and other Devanagari-based text.
*   **Asynchronous Transliteration:** Sends recognized text strings to the FastAPI backend service via `AksharamukhaClient` powered by OkHttp.
*   **State Management:** Structured architecture separating Compose UI elements from core business logic via `TranslitViewModel` using `StateFlow` and coroutines.
*   **Dependency Injection:** Configured with Dagger Hilt across application startup, ViewModels, and client classes.

---

## 🛠️ Architecture & Package Structure

```
transliterator/app/src/main/java/dev/ajithgoveas/transliterator/
├── TransliteratorApplication.kt  # Hilt Application initialization
├── MainActivity.kt               # Entrypoint Scaffold, requests camera permission
├── ocr/
│   └── MLKitTextRecognition.kt   # Devanagari ML Kit OCR client
├── translit/
│   └── AksharamukhaClient.kt     # OkHttp client for API requests
├── ui/
│   ├── MainScreen.kt             # Scaffold Coordinator binding preview & controls
│   ├── CameraPreview.kt          # CameraX surface builder
│   ├── CropOverlay.kt            # Drag-to-resize crop overlay
│   ├── components/
│   │   ├── BottomSheet.kt        # Displays OCR & Transliterated results
│   │   ├── CameraMode.kt         # Selector for camera options
│   │   ├── CaptureControls.kt    # Shutter controls
│   │   ├── ControlButton.kt      # Generic rounded action button
│   │   ├── RotaryZoomSlider.kt   # Radial focal scroll wheel UI
│   │   ├── TopControls.kt        # Top overlay bar (Flash toggles etc.)
│   │   └── ZoomButtons.kt        # iOS style zoom preset buttons
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt              # Premium Edge-to-Edge dark themed color palette
│       └── Type.kt
├── utils/
│   └── generators.kt             # Focal length math/helper functions
└── viewmodel/
    └── TranslitViewModel.kt      # StateFlow owner coordinating CameraX, OCR, and API requests
```

---

## 📋 Requirements

*   **Android Studio:** Koala (2024.1.1) or newer recommended.
*   **JDK:** Version 11 or higher.
*   **Minimum SDK:** Android 9 (API Level 28) - required for modern CameraX features.
*   **Target SDK:** Android 16 (API Level 36).
*   **Hardware Requirements:** A physical Android device or an emulator configured with a camera feed. Camera permissions must be granted on application startup.

---

## 🚀 Build & Installation

### Option 1: Run via Android Studio
1.  Open Android Studio.
2.  Select **Open** and choose the `transliterator` directory.
3.  Let the build tools sync and retrieve libraries specified in the Version Catalog (`gradle/libs.versions.toml`).
4.  Plug in a physical test device with USB debugging enabled, or launch a Camera-enabled AVD.
5.  Click the **Run** button (`Shift + F10`).

### Option 2: Run via Command Line
Build the debug APK using the Gradle Wrapper. Run this from the repository root:
```bash
# Compile and build the debug APK
./gradlew :transliterator:app:assembleDebug
```
The compiled APK will be available in:
`transliterator/app/build/outputs/apk/debug/app-debug.apk`

---

## 💡 Usage Workflow

1.  **Launch the App:** Accept the camera permission prompt when launching for the first time.
2.  **Focus Text:** Position the camera over some Devanagari text.
3.  **Adjust Zoom:** Use the preset buttons (e.g. 1x, 2x) or drag on the camera feed to activate the rotary scroll wheel for precise focus.
4.  **Crop Target:** Resize the draggable blue-bordered box overlay to frame only the line(s) of text you want to translate.
5.  **Capture & Transliterate:** Tap the central white capture shutter button.
6.  **Results:** A bottom sheet containing the extracted original text (OCR) and its transliterated equivalent (e.g., Kannada script) will slide up from the bottom.

