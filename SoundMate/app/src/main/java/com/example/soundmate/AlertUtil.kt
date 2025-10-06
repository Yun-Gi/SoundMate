package com.example.soundmate

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object AlertUtil {

    private var toast: Toast? = null

    // ✅ 1. Toast
    fun showCustomToast(context: Context, message: String, iconResId: Int) {
        val toast = Toast(context)

        // 전체 토스트 뷰 설정 (가변 길이)
        val layout = LinearLayout(context).apply {
            setBackgroundResource(R.drawable.rounded_toast_bg) // 둥근 배경 drawable
            setPadding(32, 24, 32, 24) // 원하는 패딩 설정
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // 원형 아이콘 (clipToOutline을 위해 background shape 필요)
        val imageView = ImageView(context).apply {
            setImageResource(iconResId)
            layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                rightMargin = 30
                leftMargin = 30
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = context.getDrawable(R.drawable.circle_mask) // ✅ 원형 마스크 drawable
            clipToOutline = true
        }

        // 텍스트
        val textView = TextView(context).apply {
            text = message
            setTextColor(Color.WHITE)
            textSize = 18f
            gravity = Gravity.CENTER_VERTICAL
            setPadding(32, 16, 32, 16 )
        }

        layout.addView(imageView)
        layout.addView(textView)

        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show()
    }

    // ✅ 2. Snackbar
    fun showSnackbar(scope: CoroutineScope, snackbarHostState: SnackbarHostState, message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    // ✅ 3. AlertDialog 상태관리용
    data class DialogState(
        val title: String = "",
        val message: String = "",
        val confirmText: String = "확인",
        val cancelText: String? = null,
        val onConfirm: () -> Unit = {},
        val onCancel: (() -> Unit)? = null
    )

    // Composable에서 다이얼로그 보여줄 때 사용
    @Composable
    fun AlertDialogHandler(dialogState: DialogState?, onDismiss: () -> Unit) {
        dialogState?.let {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(it.title) },
                text = { Text(it.message) },
                confirmButton = {
                    Button(onClick = {
                        it.onConfirm()
                        onDismiss()
                    }) {
                        Text(it.confirmText)
                    }
                },
                dismissButton = it.cancelText?.let { cancelText ->
                    {
                        Button(onClick = {
                            it.onCancel?.invoke()
                            onDismiss()
                        }) {
                            Text(cancelText)
                        }
                    }
                }
            )
        }
    }
}