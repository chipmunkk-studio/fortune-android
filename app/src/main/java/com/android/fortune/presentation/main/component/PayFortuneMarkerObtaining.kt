package com.android.fortune.presentation.main.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.fortune.theme.AndroidFortuneTheme

@Composable
fun PayFortuneMarkerObtaining(
    modifier: Modifier = Modifier,
    visible: Boolean,
    imageUrl: String?,
    name: String?,
) {
    val alphaAnimation = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = alphaAnimation.value }
            .background(
                color = Color.Black.copy(
                    alpha = 0.5f
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (visible) {
            Column(
                verticalArrangement = Arrangement.Center, // 수직 방향으로 중앙 정렬
                horizontalAlignment = Alignment.CenterHorizontally // 수
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("$name 줍줍 중..", style = TextStyle(color = Color.White))
            }
        }
    }
}

@Preview
@Composable
private fun MarkerObtainingPreview() {
    AndroidFortuneTheme {
        PayFortuneMarkerObtaining(
            modifier = Modifier,
            imageUrl = "https://picsum.photos/128/128/?random",
            name = "코인",
            visible = true,
        )
    }
}