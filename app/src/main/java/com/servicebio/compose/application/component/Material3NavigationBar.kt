package com.servicebio.compose.application.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.servicebio.compose.application.model.TopLevelDestination

@Composable
fun Material3NavigationBar(
    destinations: List<TopLevelDestination>,
    onNavigate2Destination: (Int) -> Unit,
    currentDestinationIndex: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        destinations.forEachIndexed { index, destination ->
            val isSelected = index == currentDestinationIndex
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate2Destination(index) },
                icon = { NavigationTabIcon(destination, isSelected) },
                label = { NavigationTabLabel(destination, isSelected) })
        }
    }
}


@Composable
private fun NavigationTabIcon(destination: TopLevelDestination, isSelected: Boolean) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(destination.animationResId))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isSelected
    )

    LottieAnimation(
        composition = composition,
        progress = { if (isSelected) progress else 0f },
        modifier = Modifier.size(30.dp)
    )
}

@Composable
private fun NavigationTabLabel(destination: TopLevelDestination, isSelected: Boolean) {
    Text(
        text = stringResource(destination.titleTextId),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
}