package com.canonal.translator.di

import com.canonal.translator.testing.FakeHistoryDataSource
import com.canonal.translator.testing.FakeTranslateClient
import com.canonal.translator.testing.FakeVoiceToTextParser
import com.canonal.translator.translate.domain.translate.TranslateUseCase
import com.canonal.translator.voice_to_text.domain.VoiceToTextParser

class TestAppModule : AppModule {
    override val historyDataSource = FakeHistoryDataSource()
    override val translateClient = FakeTranslateClient()
    override val translateUseCase = TranslateUseCase(
        client = translateClient,
        historyDataSource = historyDataSource
    )
    override val voiceToTextParser = FakeVoiceToTextParser()
}