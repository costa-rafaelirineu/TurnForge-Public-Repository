package com.turnforge.core

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (String) -> Unit): () -> Unit
