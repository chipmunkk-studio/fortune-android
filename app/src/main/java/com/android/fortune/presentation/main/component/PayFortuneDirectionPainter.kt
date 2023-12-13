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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.android.fortune.theme.AndroidFortuneTheme
import kotlin.math.absoluteValue

@Composable
fun PayFortuneDirectionPainter(headings: Float) {

    var prevHeadings by remember { mutableFloatStateOf(0f) } // 이전 방향
    var turns by remember { mutableFloatStateOf(0f) } // 누적 회전량
    var adjustedHeadings by remember { mutableFloatStateOf(0f) } // 누적 회전량

    LaunchedEffect(headings) {
        val direction = if (headings < 0) 360 + headings else headings
        var diff = direction - prevHeadings
        if (diff.absoluteValue > 180) {
            diff = if (prevHeadings > direction) {
                360 - (direction - prevHeadings).absoluteValue
            } else {
                -1 * (360 - (prevHeadings - direction).absoluteValue)
            }
        }
        turns += (diff / 360)
        prevHeadings = direction
        adjustedHeadings = turns * 360f // 회전량을 각도로 변환
    }


    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = 400f // 아크의 반지름을 200dp로 설정
        // 그라데이션 정의
        val gradient = Brush.radialGradient(
            colors = listOf(
                Color.Green.copy(alpha = 0.3f),
                Color.Green.copy(alpha = 0.2f),
                Color.Green.copy(alpha = 0.1f),
                Color.Green.copy(alpha = 0.05f),
                Color.Green.copy(alpha = 0.0f)
            ),
            center = Offset(centerX, centerY),
            radius = radius
        )
        // 나침반 방향에 따라 캔버스 회전
        rotate(degrees = adjustedHeadings, pivot = Offset(centerX, centerY)) {
            drawArc(
                brush = gradient,
                startAngle = 145f, // 시작 각도
                sweepAngle = 50f, // 스윕 각도
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2) // 아크의 크기를 늘림
            )
        }
    }
}

@Preview
@Composable
private fun PayFortuneDirectionPainterPreview() {
    AndroidFortuneTheme {
        PayFortuneDirectionPainter(0F)
    }
}