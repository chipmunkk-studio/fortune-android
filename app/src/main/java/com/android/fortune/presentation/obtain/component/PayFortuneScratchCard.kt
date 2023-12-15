package com.android.fortune.presentation.obtain.component

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DraggedPath(
    val path: Path,
    val points: MutableList<Offset> = mutableListOf()
)

@Composable
fun PayFortuneScratchCard(
    overlayImage: ImageBitmap,
    baseImage: ImageBitmap,
    onScratchEnd: () -> Unit,
    isCloseScratchBox: Boolean,
) {
    val density = LocalDensity.current
    var currentPathState by remember { mutableStateOf(DraggedPath(path = Path())) }
    val radiusPixels = with(density) { 32.dp.toPx() }
    var scratchedArea by remember { mutableFloatStateOf(0f) }
    val circleArea = Math.PI.toFloat() * radiusPixels
    val totalArea = overlayImage.width * overlayImage.height.toFloat() // 픽셀 단위로 계산

    LaunchedEffect(scratchedArea) {
        val scratchedPercentage = (scratchedArea / totalArea) * 100
        if (scratchedPercentage >= 70) {
            onScratchEnd()
        }
    }

    AnimatedVisibility(
        visible = !isCloseScratchBox,
        enter = fadeIn(initialAlpha = 0f, animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(targetAlpha = 0f, animationSpec = tween(durationMillis = 300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray)
        ) {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .fillMaxSize()
                    .align(alignment = Alignment.Center)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val position = change.position
                            if (!currentPathState.points.any { it.distance(position) < radiusPixels / 7 }) {
                                scratchedArea += circleArea
                            }
                            currentPathState = currentPathState.copy(
                                path = Path().also {
                                    it.addPath(currentPathState.path)
                                    it.addOval(
                                        oval = Rect(
                                            left = position.x - radiusPixels,
                                            top = position.y - radiusPixels,
                                            right = position.x + radiusPixels,
                                            bottom = position.y + radiusPixels
                                        )
                                    )
                                },
                                points = mutableListOf<Offset>().apply {
                                    addAll(currentPathState.points)
                                    add(position)
                                }
                            )
                        }
                    }
            ) {
                val imageSize = IntSize(width = size.width.toInt(), height = size.height.toInt())
                val padding = with(density) { 24.dp.toPx() }
                val paddedSize = IntSize(
                    width = (imageSize.width - padding * 2).toInt(),
                    height = (imageSize.height - padding * 2).toInt()
                )
                val paddedOffset = IntOffset(padding.toInt(), padding.toInt())

                clipPath(
                    path = currentPathState.path,
                    clipOp = ClipOp.Difference,
                ) {
                    // 베이스 이미지를 패딩을 적용하여 그립니다.
                    drawImage(
                        image = overlayImage,
                        dstSize = imageSize
                    )
                }

                clipPath(
                    path = currentPathState.path,
                    clipOp = ClipOp.Intersect
                ) {
                    drawImage(
                        baseImage,
                        dstOffset = paddedOffset,
                        dstSize = paddedSize
                    )
                }
            }


        }
    }

}

fun Offset.distance(to: Offset): Float {
    return (this - to).getDistance()
}

@Composable
fun loadNetworkImage(url: String): ImageBitmap? {
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalContext.current

    LaunchedEffect(url) {
        image = context.loadNetworkImage(url)
    }

    return image
}

suspend fun Context.loadNetworkImage(url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            Coil.imageLoader(this@loadNetworkImage).execute(
                ImageRequest.Builder(this@loadNetworkImage)
                    .data(url)
                    .build()
            ).drawable?.toBitmap()?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_4)
fun ImageScratchPreview() {
    val overlayImageUrl = "https://picsum.photos/128/128/?random"
    val baseImageUrl =
        "https://hcpyrleocxaejkqqibik.supabase.co/storage/v1/object/public/ingredients/fortune_cookie.webp"

    val overlayImage = loadNetworkImage(overlayImageUrl)
    val baseImage = loadNetworkImage(baseImageUrl)

    if (overlayImage != null && baseImage != null) {
        PayFortuneScratchCard(
            overlayImage = overlayImage,
            baseImage = baseImage,
            onScratchEnd = {},
            isCloseScratchBox = false
        )
    }
}