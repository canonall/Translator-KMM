package com.canonal.translator.core.presentation

import com.canonal.translator.core.domain.language.Language

// we don't have a unified type for image(county flag)
// because we use int(id) in android and string in iOS
// so, it is implemented in androidMain and iosMain
expect class UiLanguage {
    val language: Language

    companion object {
        fun byCode(languageCode: String): UiLanguage
        val allLanguages: List<UiLanguage>
    }
}