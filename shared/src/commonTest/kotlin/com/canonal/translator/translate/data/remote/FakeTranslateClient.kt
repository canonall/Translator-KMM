package com.canonal.translator.translate.data.remote

import com.canonal.translator.core.domain.language.Language
import com.canonal.translator.translate.domain.translate.TranslateClient

class FakeTranslateClient: TranslateClient {

    var translatedTest = "test translation"

    override suspend fun translate(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): String {
        return translatedTest
    }
}