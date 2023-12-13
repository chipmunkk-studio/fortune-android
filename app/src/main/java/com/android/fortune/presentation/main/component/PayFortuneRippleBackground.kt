package com.android.fortune.presentation.main.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.fortune.theme.AndroidFortuneTheme

@Composable
fun FortuneMainRippleBackground(
    modifier: Modifier,
    rippleColor: Color = Color.Green,
    rippleCount: Int = 3,
    rippleDurationTime: Int = 5000,
    rippleScale: Float = 6.0f,
    initialAlpha: Float = 0.2f, // 시작 투명도
    initialScale: Float = 0f // 시작 크기
) {
    val minSize = 50.dp // 지정한 크기

    for (i in 0 until rippleCount) {
        val delay = (rippleDurationTime / rippleCount) * i
        val animationSpec = infiniteRepeatable<Float>(
            animation = tween(
                rippleDurationTime,
                easing = FastOutSlowInEasing,
                delayMillis = delay
            ),
            repeatMode = RepeatMode.Restart
        )

        RippleEffect(
            modifier = modifier,
            rippleColor = rippleColor,
            rippleScale = rippleScale,
            minSize = minSize,
            animationSpec = animationSpec,
            initialAlpha = initialAlpha,
            initialScale = initialScale
        )
    }
}

@Composable
private fun RippleEffect(
    modifier: Modifier,
    rippleColor: Color,
    rippleScale: Float,
    minSize: Dp,
    animationSpec: InfiniteRepeatableSpec<Float>,
    initialAlpha: Float,
    initialScale: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animation = infiniteTransition.animateFloat(
        initialValue = initialScale,
        targetValue = rippleScale,
        animationSpec = animationSpec, label = ""
    )

    Canvas(modifier = modifier.size(minSize)) {
        val scale = animation.value
        val alpha = (1 - (scale - initialScale) / (rippleScale - initialScale)) * initialAlpha

        drawCircle(
            color = rippleColor.copy(alpha = alpha),
            radius = (minSize.toPx() / 2) * scale,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

@Preview
@Composable
private fun RippleEffectPreview() {
    AndroidFortuneTheme {
        FortuneMainRippleBackground(
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}