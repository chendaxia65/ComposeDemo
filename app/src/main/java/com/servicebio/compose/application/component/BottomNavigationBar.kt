package com.servicebio.compose.application.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.servicebio.compose.application.model.TopLevelDestination

@Composable
fun BottomNavigationBar(
    destinations: List<TopLevelDestination>,
    onNavigate2Destination: (Int) -> Unit,
    currentDestinationIndex: Int,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shadowElevation: Dp = 10.dp
) {

    val shadowElevationPx = with(LocalDensity.current) { shadowElevation.toPx() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.shadowElevation = shadowElevationPx
            }
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        destinations.forEachIndexed { index, destination ->
            val isSelected = index == currentDestinationIndex

            //Compose中组件重组是基于参数+State，如果参数没有改变NavigationTab就不会被重新绘制
            //所以NavigationTab需要重组的只会有两个
            NavigationTab(destination = destination, isSelected = isSelected) {
                onNavigate2Destination(index)
            }
        }
    }
}


@Composable
private fun RowScope.NavigationTab(
    destination: TopLevelDestination,
    isSelected: Boolean,
    onNavigateTap: () -> Unit
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(destination.animationResId))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isSelected
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 5.dp)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    tryAwaitRelease()
                }, onTap = { onNavigateTap() })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { if (isSelected) progress else 0f },
            modifier = Modifier.size(30.dp)
        )

        Text(
            text = stringResource(destination.titleTextId),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}