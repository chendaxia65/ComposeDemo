package com.servicebio.compose.application.model

import com.servicebio.compose.application.component.SymbolAnnotationType

sealed class SheetEvent {

    class ShowSheet(val type: SymbolAnnotationType,val item: String) : SheetEvent()

    object DismissSheet : SheetEvent()
}