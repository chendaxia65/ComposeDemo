package com.servicebio.compose.application.ext

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier


fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.then(Modifier.clickable(onClick = onClick, interactionSource = null, indication = null))