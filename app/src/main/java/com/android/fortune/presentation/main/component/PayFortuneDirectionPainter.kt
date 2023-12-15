package com.android.fortune.presentation.main.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.android.fortune.theme.AndroidFortuneTheme
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PayFortuneDirectionPainter() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = 420f // 아크의 반지름

        // 부채꼴의 시작점과 끝점 계산
        val startAngle = 145f // 시작 각도
        val sweepAngle = 75f // 스윕 각도
        val endAngle = startAngle + sweepAngle
        val gradientStart = Offset(
            x = centerX + radius * cos(Math.toRadians(startAngle.toDouble())).toFloat(),
            y = centerY + radius * sin(Math.toRadians(startAngle.toDouble())).toFloat()
        )
        val gradientEnd = Offset(
            x = centerX + radius * cos(Math.toRadians(endAngle.toDouble())).toFloat(),
            y = centerY + radius * sin(Math.toRadians(endAngle.toDouble())).toFloat()
        )

        val gradient = Brush.linearGradient(
            colors = listOf(
                Color.Green.copy(alpha = 0.00f), // 아래쪽: 완전 투명
                Color.Green.copy(alpha = 0.005f),
                Color.Green.copy(alpha = 0.01f),
                Color.Green.copy(alpha = 0.03f),
                Color.Green.copy(alpha = 0.05f),
                Color.Green.copy(alpha = 0.1f),
                Color.Green.copy(alpha = 0.2f), // 중간: 약간 불투명
                Color.Green.copy(alpha = 0.3f), // 중간: 더 불투명
                Color.Green.copy(alpha = 0.4f), // 위쪽: 더 불투명
                Color.Green.copy(alpha = 0.5f)  // 위쪽: 가장 진하고 불투명
            ),
            start = gradientStart,
            end = gradientEnd
        )

        // 캔버스 회전
        rotate(degrees = rotation, pivot = Offset(centerX, centerY)) {
            drawArc(
                brush = gradient,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
    }
}

@Preview
@Composable
private fun PayFortuneDirectionPainterPreview() {
    AndroidFortuneTheme {
        PayFortuneDirectionPainter()
    }
}