package com.canonal.translator.android.di

import android.app.Application
import com.canonal.translator.database.TranslateDatabase
import com.canonal.translator.translate.data.history.SqlDelightHistoryDataSource
import com.canonal.translator.translate.data.local.DatabaseDriverFactory
import com.canonal.translator.translate.data.remote.HttpClientFactory
import com.canonal.translator.translate.data.translate.KtorTranslateClient
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.translate.TranslateClient
import com.canonal.translator.translate.domain.translate.TranslateUseCase
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClientFactory().create()

    }

    @Provides
    @Singleton
    fun provideTranslateClient(httpClient: HttpClient): TranslateClient {
        return KtorTranslateClient(httpClient)
    }

    @Provides
    @Singleton
    fun provideDatabaseDriver(app: Application): SqlDriver {
        return DatabaseDriverFactory(context = app).create()
    }

    @Provides
    @Singleton
    fun provideHistoryDataSource(driver: SqlDriver): HistoryDataSource {
        return SqlDelightHistoryDataSource(db = TranslateDatabase(driver = driver))
    }

    @Provides
    @Singleton
    fun provideTranslateUseCase(
        client: TranslateClient,
        historyDataSource: HistoryDataSource
    ): TranslateUseCase {
        return TranslateUseCase(
            client = client,
            historyDataSource = historyDataSource
        )
    }
}