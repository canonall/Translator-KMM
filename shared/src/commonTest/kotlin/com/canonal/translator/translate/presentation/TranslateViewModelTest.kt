package com.canonal.translator.translate.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.canonal.translator.core.presentation.UiLanguage
import com.canonal.translator.translate.data.local.FakeHistoryDataSource
import com.canonal.translator.translate.data.remote.FakeErrorTranslateClient
import com.canonal.translator.translate.data.remote.FakeTranslateClient
import com.canonal.translator.translate.domain.history.HistoryItem
import com.canonal.translator.translate.domain.translate.TranslateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class TranslateViewModelTest {

    private lateinit var viewModel: TranslateViewModel
    private lateinit var client: FakeTranslateClient
    private lateinit var dataSource: FakeHistoryDataSource
    private lateinit var translateUseCase: TranslateUseCase

    // runs before each test case
    // reset the viewmodel state before each test case
    @BeforeTest
    fun setUp() {
        client = FakeTranslateClient()
        dataSource = FakeHistoryDataSource()
        translateUseCase = TranslateUseCase(
            client = client,
            historyDataSource = dataSource
        )

        viewModel = TranslateViewModel(
            translateUseCase = translateUseCase,
            historyDataSource = dataSource,
            coroutineScope = CoroutineScope(Dispatchers.Default)
        )
    }

    @Test
    fun `state and history items are properly combined`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            val historyItem = HistoryItem(
                id = 0,
                fromLanguageCode = "en",
                fromText = "from",
                toLanguageCode = "de",
                toText = "to"
            )
            dataSource.insertHistoryItem(item = historyItem)

            val state = awaitItem()

            val expectedItem = UiHistoryItem(
                id = historyItem.id!!,
                fromText = historyItem.fromText,
                fromLanguage = UiLanguage.byCode(historyItem.fromLanguageCode),
                toText = historyItem.toText,
                toLanguage = UiLanguage.byCode(historyItem.toLanguageCode)
            )

            assertThat(state.history.first()).isEqualTo(expected = expectedItem)
        }
    }

    @Test
    fun `translate success - state properly updated`() = runBlocking {
        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "test"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val resultState = awaitItem()
            assertThat(resultState.isTranslating).isFalse()
            assertThat(resultState.toText).isEqualTo(client.translatedTest)
        }
    }

    @Test
    fun `translate server error - state properly updated`() = runBlocking {
        val errorClient  = FakeErrorTranslateClient()
        viewModel = TranslateViewModel(
            translateUseCase = TranslateUseCase(
                client = errorClient,
                historyDataSource = dataSource
            ),
            historyDataSource = dataSource,
            coroutineScope = CoroutineScope(Dispatchers.Default)
        )

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "test"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val resultState = awaitItem()
            assertThat(resultState.isTranslating).isFalse()
            assertThat(resultState.error).isEqualTo(errorClient.error)
        }
    }
}