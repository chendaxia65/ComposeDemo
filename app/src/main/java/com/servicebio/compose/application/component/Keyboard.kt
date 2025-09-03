package com.servicebio.compose.application.component

import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

data class KeyboardSate(val imeHeight: Dp, val imeHeightPx: Int, val imePadding: Dp) {
    companion object {
        fun of(density: Density, navigationBars: WindowInsets, imeHeight: Dp): KeyboardSate {
            return KeyboardSate(
                imeHeight,
                with(density) { imeHeight.toPx() }.toInt(),
                with(density) { imeHeight - navigationBars.getBottom(density).toDp() }
            )
        }
    }
}

/**
 * 只关注于键盘高度
 */
@OptIn(FlowPreview::class)
@Composable
fun monitorKeyboardHeight(): State<Dp> {
    val ime = WindowInsets.ime
    val density = LocalDensity.current

    var imeHeight by remember { mutableStateOf(336.dp) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        LaunchedEffect(ime) {
            snapshotFlow {
                ime.getBottom(density)
            }.debounce(30L).collect {
                if (it > 100) {
                    val imeBottom = with(density) { it.toDp() }
                    if (imeBottom != imeHeight) {
                        imeHeight = imeBottom
                    }
                }
            }
        }
    } else {
        val view = LocalView.current
        DisposableEffect(Unit) {
            val listener = GlobalLayoutListener(view) {
                if (it > 100) {
                    val imeBottom = with(density) { it.toDp() }
                    if (imeBottom != imeHeight) {
                        imeHeight = imeBottom
                    }
                }
            }

            view.viewTreeObserver.addOnGlobalLayoutListener(listener)

            onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
        }
    }

    return rememberUpdatedState(imeHeight)
}


@Composable
fun rememberKeyboardState(onAnimationEnd: (Dp) -> Unit): State<Dp> {
    //android:windowSoftInputMode="adjustResize"

    val density = LocalDensity.current
    val ime = WindowInsets.ime.asPaddingValues()
    val navigationBars = WindowInsets.navigationBars

    var imeHeight by remember { mutableStateOf(0.dp) }

    val view = LocalView.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        DisposableEffect(Unit) {
            val listener = WindowInsetsAnimationCallback(onProgress = {
                imeHeight = with(density) { it.toDp() }
            }, onAnimationEnd = {
                onAnimationEnd(ime.calculateBottomPadding())
            })

            ViewCompat.setWindowInsetsAnimationCallback(view, listener)

            onDispose {
                ViewCompat.setWindowInsetsAnimationCallback(view, null)
            }
        }
    } else {
        DisposableEffect(Unit) {
            val listener = GlobalLayoutListener(view, navigationBars.getBottom(density)) {
                imeHeight = with(density) { it.toDp() }
                onAnimationEnd(imeHeight)
            }

            view.viewTreeObserver.addOnGlobalLayoutListener(listener)

            onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
        }
    }

    return rememberUpdatedState(imeHeight)
}

@RequiresApi(Build.VERSION_CODES.R)
private class WindowInsetsAnimationCallback(
    private val onProgress: (Int) -> Unit = {},
    private val onAnimationEnd: () -> Unit = {}
) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        onAnimationEnd()
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat?>
    ): WindowInsetsCompat {
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        onProgress(imeInsets.bottom)
        return insets
    }
}

class GlobalLayoutListener(
    private val decorView: View,
    private val navigationBar: Int = -1,//-1 表示忽略键盘收起时的回调
    private val onAnimationEnd: (Int) -> Unit = {}
) :
    ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
        val rect = android.graphics.Rect()
        decorView.getWindowVisibleDisplayFrame(rect)

        val screenHeight = decorView.height
        val keyboardHeight = screenHeight - rect.bottom

        //键盘展开
        if (keyboardHeight > 180) onAnimationEnd(keyboardHeight)

        //键盘收起
        if (navigationBar >= 0) {
            if (keyboardHeight == navigationBar) onAnimationEnd(0)
        }

    }

}