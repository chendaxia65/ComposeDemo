package com.servicebio.compose.application.ext

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow


fun <T> NavHostController.popBackStackWithResult(key: String, result: T) {
    previousBackStackEntry?.savedStateHandle?.set(key, result)
    popBackStack()
}

fun <T> NavHostController.getResultStateFlow(key: String, initialValue: T): StateFlow<T>? {
    return currentBackStackEntry?.savedStateHandle?.getStateFlow(key, initialValue)
}

fun <T> NavHostController.removeKey(key: String) {
    currentBackStackEntry?.savedStateHandle?.remove<T>(key)
}