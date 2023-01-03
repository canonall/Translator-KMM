package com.canonal.translator.di

import com.canonal.translator.database.TranslateDatabase
import com.canonal.translator.translate.data.history.SqlDelightHistoryDataSource
import com.canonal.translator.translate.data.local.DatabaseDriverFactory
import com.canonal.translator.translate.data.remote.HttpClientFactory
import com.canonal.translator.translate.data.translate.KtorTranslateClient
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.translate.TranslateClient
import com.canonal.translator.translate.domain.translate.TranslateUseCase

class AppModule {
     val historyDataSource: HistoryDataSource by lazy {
        SqlDelightHistoryDataSource(
            db = TranslateDatabase(
                driver = DatabaseDriverFactory().create()
            )
        )
    }

    private val translateClient: TranslateClient by lazy {
        KtorTranslateClient(
            httpClient = HttpClientFactory().create()
        )
    }

    val translateUseCase: TranslateUseCase by lazy {
        TranslateUseCase(client = translateClient, historyDataSource = historyDataSource)
    }
}