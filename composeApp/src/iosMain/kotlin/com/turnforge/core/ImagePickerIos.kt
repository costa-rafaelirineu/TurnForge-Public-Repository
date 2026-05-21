package com.turnforge.core

import androidx.compose.runtime.Composable

// Basic implementation for iOS. In a real app, this would need a more robust delegate handling.
@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (String) -> Unit): () -> Unit {
    return {
        // Placeholder for iOS implementation
        // Typically requires a UIViewController to present the picker
    }
}
