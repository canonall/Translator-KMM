package com.canonal.translator.translate.data.remote

import com.canonal.translator.core.domain.language.Language
import com.canonal.translator.translate.domain.translate.TranslateClient
import com.canonal.translator.translate.domain.translate.TranslateError
import com.canonal.translator.translate.domain.translate.TranslateException

class FakeErrorTranslateClient: TranslateClient {

    var error = TranslateError.SERVER_ERROR

    override suspend fun translate(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): String {
        throw TranslateException(error = error)
    }
}