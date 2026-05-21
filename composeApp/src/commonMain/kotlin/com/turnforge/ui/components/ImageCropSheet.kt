package com.turnforge.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.turnforge.core.cropImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropSheet(
    imageUri: String,
    onResult: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var isProcessing by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val painter = rememberAsyncImagePainter(imageUri)
    val painterState by painter.state.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "AJUSTAR IMAGEM",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                "Arraste e use dois dedos para zoom",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Crop Area (Square)
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .onGloballyPositioned { containerSize = it.size }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)

                            // Bounds for panning
                            val maxOffsetX = (containerSize.width * (scale - 1)) / 2f
                            val maxOffsetY = (containerSize.height * (scale - 1)) / 2f

                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                                y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                            )
                        }
                    }
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.Crop
                )

                // Border for the crop area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isProcessing) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = {
                        val successState = painterState as? AsyncImagePainter.State.Success
                        val intrinsicSize = successState?.painter?.intrinsicSize

                        if (intrinsicSize == null || intrinsicSize.width <= 0f || intrinsicSize.height <= 0f) {
                            onDismiss()
                            return@Button
                        }

                        isProcessing = true
                        scope.launch {
                            val imageAspectRatio = intrinsicSize.width / intrinsicSize.height
                            val containerAspectRatio =
                                containerSize.width.toFloat() / containerSize.height.toFloat()

                            // When using ContentScale.Crop, the image is scaled to fill the container.
                            // One dimension matches the container, the other overflows.
                            val (baseDrawWidth, baseDrawHeight) = if (imageAspectRatio > containerAspectRatio) {
                                // Image is wider than container, height matches, width overflows
                                (containerSize.height * imageAspectRatio) to containerSize.height.toFloat()
                            } else {
                                // Image is taller than container, width matches, height overflows
                                containerSize.width.toFloat() to (containerSize.width / imageAspectRatio)
                            }

                            // The actual size of the image as rendered (including the extra scale from gestures)
                            val finalDrawWidth = baseDrawWidth * scale
                            val finalDrawHeight = baseDrawHeight * scale

                            // How much of the image is visible in the container (normalized 0 to 1)
                            val normWidth = containerSize.width / finalDrawWidth
                            val normHeight = containerSize.height / finalDrawHeight

                            // The offset is relative to the center of the container
                            // Center of image in normalized coordinates is 0.5
                            val normOffsetX = 0.5f - (normWidth / 2f) - (offset.x / finalDrawWidth)
                            val normOffsetY =
                                0.5f - (normHeight / 2f) - (offset.y / finalDrawHeight)

                            val rect = Rect(
                                left = normOffsetX.coerceIn(0f, 1f),
                                top = normOffsetY.coerceIn(0f, 1f),
                                right = (normOffsetX + normWidth).coerceIn(0f, 1f),
                                bottom = (normOffsetY + normHeight).coerceIn(0f, 1f)
                            )

                            val resultUri = cropImage(imageUri, rect)
                            onResult(resultUri)
                            isProcessing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF3B82F6),
                                        Color(0xFF2DD4BF)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CONFIRMAR RECORTE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.1f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text(
                    "CANCELAR",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
