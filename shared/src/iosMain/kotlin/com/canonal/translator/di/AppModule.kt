package com.canonal.translator.di

import com.canonal.translator.database.TranslateDatabase
import com.canonal.translator.translate.data.history.SqlDelightHistoryDataSource
import com.canonal.translator.translate.data.local.DatabaseDriverFactory
import com.canonal.translator.translate.data.remote.HttpClientFactory
import com.canonal.translator.translate.data.translate.KtorTranslateClient
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.translate.TranslateClient
import com.canonal.translator.translate.domain.translate.TranslateUseCase
import com.canonal.translator.voice_to_text.domain.VoiceToTextParser

interface AppModule {
    val historyDataSource: HistoryDataSource
    val translateClient: TranslateClient
    val translateUseCase: TranslateUseCase
    val voiceToTextParser: VoiceToTextParser
}

class AppModuleImpl(
    parser: VoiceToTextParser
) : AppModule {
    override val historyDataSource: HistoryDataSource by lazy {
        SqlDelightHistoryDataSource(
            db = TranslateDatabase(
                driver = DatabaseDriverFactory().create()
            )
        )
    }

    override val translateClient: TranslateClient by lazy {
        KtorTranslateClient(
            httpClient = HttpClientFactory().create()
        )
    }

    override val translateUseCase: TranslateUseCase by lazy {
        TranslateUseCase(client = translateClient, historyDataSource = historyDataSource)
    }
    override val voiceToTextParser = parser
}