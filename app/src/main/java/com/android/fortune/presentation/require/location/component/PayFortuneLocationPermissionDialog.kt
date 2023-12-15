package com.android.fortune.presentation.require.location.component

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PayFortuneLocationPermissionDialog(
    activity: Activity?,
) = activity?.let {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("권한 필요") },
        text = { Text("이 기능을 사용하려면 위치 권한이 필요합니다. 설정 화면으로 이동하여 권한을 허용해주세요.") },
        confirmButton = {
            TextButton(
                onClick = {
                    // 사용자를 앱 설정 화면으로 이동
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", it.packageName, null)
                    intent.data = uri
                    it.startActivity(intent)
                }
            ) {
                Text("설정")
            }
        },
        dismissButton = {
            TextButton(onClick = { /* 다이얼로그 닫기 */ }) {
                Text("취소")
            }
        }
    )
}