package com.servicebio.compose.application

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex = _currentPageIndex.asStateFlow()

    fun updateDestination(index: Int) {
        _currentPageIndex.value = index
    }
}