package com.canonal.translator.translate.presentation

import com.canonal.translator.core.presentation.UiLanguage

data class UiHistoryItem(
    val id: Long,
    val fromText: String,
    val toText: String,
    val fromLanguage: UiLanguage,
    val toLanguage: UiLanguage
) {
    companion object {
        val previewItem = UiHistoryItem(
            id = 0,
            fromText = "Hello",
            toText = "Hallo",
            fromLanguage = UiLanguage.byCode("en"),
            toLanguage = UiLanguage.byCode("de")
        )
    }
}
