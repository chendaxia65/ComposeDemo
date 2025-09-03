package com.servicebio.compose.application.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.servicebio.compose.application.R

enum class TopLevelDestination(
    @StringRes val titleTextId: Int,
    @RawRes val animationResId: Int,
    val route: String
) {
    HOME(R.string.home, R.raw.home, "home"),
    CATEGORY(R.string.category, R.raw.category, "category"),
    CART(R.string.cart, R.raw.cart, "cart"),
    ME(R.string.me, R.raw.me, "me")
}