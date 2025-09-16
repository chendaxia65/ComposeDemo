package com.servicebio.compose.application.component

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

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

    var imeHeight by remember { mutableStateOf(288.dp) }

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun rememberPanelPadding2(
    keyboardManager: KeyboardManager,
    isPanelOpened: Boolean
): State<Dp> {
    val density = LocalDensity.current
    val navigationBars = WindowInsets.navigationBars

    val lastShown = remember { Ref<Boolean>().apply { value = false } }
    val lastHeight = remember { Ref<Dp>().apply { value = 0.dp } }


    val keyboardStateRef = remember {
        Ref<KeyboardSate>().apply {
            value = KeyboardSate.of(density, navigationBars, 288.dp)
        }
    }

    val keyboardState = keyboardStateRef.value!!


    LaunchedEffect(Unit) {
        keyboardManager.addOnAnimationEndListener {
            if (it > 0.dp) {
                if (keyboardState.imeHeight != it) {
                    keyboardStateRef.value =
                        KeyboardSate.of(density, navigationBars, it)
                }
            }
            lastShown.value = it > 0.dp
        }
    }
    val animPanelHeight by animateDpAsState(if (isPanelOpened) keyboardState.imePadding else 0.dp)

    val updateDone: (Dp) -> Unit = {
        if (it == 0.dp || it == keyboardState.imePadding) {
            lastHeight.value = it
        }
    }

    val oldPanelState = lastHeight.value == keyboardState.imePadding

    //如果开启Panel 并且 Panel已经是开启状态 就返回固定Padding
    if (isPanelOpened && oldPanelState) {
        return rememberUpdatedState(keyboardState.imePadding)
    } else if (!WindowInsets.isImeVisible && lastShown.value == false) {//如果键盘没有展开并且之前也没有被展开，这次操作视为开启或隐藏Panel，执行animateDpAsState
        updateDone(animPanelHeight)
        return rememberUpdatedState(animPanelHeight)
    }

    val imeBottom by keyboardManager.height
    val navBottom = WindowInsets.navigationBars.asPaddingValues()
    val imeHeight = (imeBottom - navBottom.calculateBottomPadding()).coerceAtLeast(0.dp)

    Log.d("TAG", "rememberPanelPadding2: imeHeight $imeHeight")

    //如果在键盘显示之前Panel已经是开启状态，就使用固定Padding
    val height = if ((WindowInsets.isImeVisible && oldPanelState)) {
        if (imeHeight > keyboardState.imePadding) imeHeight else keyboardState.imePadding
    } else {
        imeHeight
    }

    updateDone(height)

    return rememberUpdatedState(height)
}

@Composable
fun rememberKeyboardManager(): KeyboardManager {
    //android:windowSoftInputMode="adjustResize"

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val ime = WindowInsets.ime.asPaddingValues()
    val navigationBars = WindowInsets.navigationBars
    val view = LocalView.current

    var imeHeight by remember { mutableStateOf(0.dp) }
    val heightState = rememberUpdatedState(imeHeight)
    val manager = remember { KeyboardManager(heightState) }

    val toDp: (Int) -> Dp = { with(density) { it.toDp() } }


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        DisposableEffect(Unit) {
            val listener = WindowInsetsAnimationCallback(onProgress = {
                imeHeight = toDp(it)
                Log.d("TAG", "rememberPanelPadding2: onProgress")

                manager.notifyMovementDirection(imeHeight)
            }, onAnimationEnd = {
                scope.launch {
                    delay(28)
                    Log.d("TAG", "rememberPanelPadding2: onAnimationEnd")
                    manager.notifyAnimationEnd(ime.calculateBottomPadding())
                }
            })

            ViewCompat.setWindowInsetsAnimationCallback(view, listener)

            onDispose {
                ViewCompat.setWindowInsetsAnimationCallback(view, null)
            }
        }
    } else {
        DisposableEffect(Unit) {
            val listener =
                GlobalLayoutListener(
                    view,
                    navigationBars.getBottom(density),
                    onProgress = {
                        manager.notifyMovementDirection(toDp(it))
                    },
                    onAnimationEnd = {
                        imeHeight = toDp(it)
                        scope.launch {
                            delay(28)
                            manager.notifyAnimationEnd(imeHeight)
                        }
                    })

            view.viewTreeObserver.addOnGlobalLayoutListener(listener)

            onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
        }
    }

    return manager
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
        Log.e("WindowInsetsAnimationCallback", "onProgress: ")
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        onProgress(imeInsets.bottom)
        return insets
    }
}

private class GlobalLayoutListener(
    private val decorView: View,
    private val navigationBar: Int = -1,//-1 表示忽略键盘收起时的回调
    private val onProgress: (Int) -> Unit = {},
    private val onAnimationEnd: (Int) -> Unit = {}
) :
    ViewTreeObserver.OnGlobalLayoutListener {
    private var oldKeyboardHeight = -1

    override fun onGlobalLayout() {
        val rect = android.graphics.Rect()
        decorView.getWindowVisibleDisplayFrame(rect)

        val screenHeight = decorView.height
        val keyboardHeight = screenHeight - rect.bottom

        if (oldKeyboardHeight == keyboardHeight) return

        oldKeyboardHeight = keyboardHeight

        onProgress(keyboardHeight)
        //键盘展开
        if (keyboardHeight > 180) onAnimationEnd(keyboardHeight)

        //键盘收起
        if (navigationBar >= 0) {
            if (keyboardHeight == navigationBar) onAnimationEnd(0)
        }

    }

}

class KeyboardManager internal constructor(val height: State<Dp>) {

    @IntDef(value = [DIRECTION_UNKNOW, DIRECTION_DOWN, DIRECTION_UP])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Direction

    @Direction
    private var movementDirection: Int = DIRECTION_UNKNOW

    private var heightCached = M

    companion object {
        const val DIRECTION_DOWN: Int = 0 //键盘收起，向下运动
        const val DIRECTION_UP: Int = 1 //键盘展开，向上运动
        const val DIRECTION_UNKNOW: Int = -1

        private val M = (-1).dp
    }

    private val animationListeners = mutableListOf<(Dp) -> Unit>()
    private val directionListeners = mutableListOf<(Int) -> Unit>()


    /**
     * 监听键盘动画结束
     */
    fun addOnAnimationEndListener(onAnimationEnd: (Dp) -> Unit) {
        if (!animationListeners.contains(onAnimationEnd)) {
            animationListeners += onAnimationEnd
        }
    }

    /**
     * 监听键盘初始运动方向
     */
    fun addOnDirectionChangedListener(onDirectionChanged: (Int) -> Unit) {
        if (!directionListeners.contains(onDirectionChanged)) {
            directionListeners += onDirectionChanged
        }
    }

    fun isMovement() = movementDirection != DIRECTION_UNKNOW

    internal fun notifyAnimationEnd(height: Dp) {
        heightCached = M
        movementDirection = DIRECTION_UNKNOW
        animationListeners.forEach { it.invoke(height) }
    }

    internal fun notifyMovementDirection(height: Dp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (heightCached == M || heightCached == height) {
                heightCached = height
            } else {
                val direction = if (height > heightCached) DIRECTION_UP else DIRECTION_DOWN
                if (movementDirection != direction) {
                    movementDirection = direction
                    directionListeners.forEach { it.invoke(movementDirection) }
                }
            }
        } else {
            val direction = if (height > 100.dp) DIRECTION_UP else DIRECTION_DOWN
            if (movementDirection != direction) {
                movementDirection = direction
                directionListeners.forEach { it.invoke(movementDirection) }
            }
        }
    }

}