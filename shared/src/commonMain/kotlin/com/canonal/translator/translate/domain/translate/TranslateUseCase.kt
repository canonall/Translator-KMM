package com.canonal.translator.translate.domain.translate

import com.canonal.translator.core.domain.language.Language
import com.canonal.translator.core.domain.util.Resource
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.history.HistoryItem

class TranslateUseCase(
    private val client: TranslateClient,
    private val historyDataSource: HistoryDataSource
) {
    suspend fun execute(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): Resource<String> {
        return try {
            val translatedText = client.translate(
                fromLanguage = fromLanguage,
                fromText = fromText,
                toLanguage = toLanguage
            )

            historyDataSource.insertHistoryItem(
                HistoryItem(
                    //sqldelight will generate the id
                    id = null,
                    fromLanguageCode = fromLanguage.languageCode,
                    fromText = fromText,
                    toLanguageCode = toLanguage.languageCode,
                    toText = translatedText
                )
            )

            Resource.Success(translatedText)
        } catch (exception: TranslateException) {
            exception.printStackTrace()
            Resource.Error(throwable = exception)
        }
    }
}