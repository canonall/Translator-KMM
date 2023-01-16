package com.canonal.translator.translate.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.*
import com.canonal.translator.core.presentation.UiLanguage
import com.canonal.translator.translate.data.local.FakeErrorHistoryDataSource
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
    fun `change translation text`() = runBlocking {
        val originalText = "original text"

        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(originalText))
            val changedTextState = awaitItem()
            assertThat(changedTextState.fromText).assertThat(originalText)
            assertThat(changedTextState.isTranslating).isFalse()
            assertThat(changedTextState.toText).isNull()
        }
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
    fun `translate success`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

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
        val errorClient = FakeErrorTranslateClient()
        viewModel = TranslateViewModel(
            translateUseCase = TranslateUseCase(
                client = errorClient,
                historyDataSource = dataSource
            ),
            historyDataSource = dataSource,
            coroutineScope = CoroutineScope(Dispatchers.Default)
        )

        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

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

    @Test
    fun `error when inserting to dataSource - state properly updated`() = runBlocking {
        val errorDataSource = FakeErrorHistoryDataSource()
        viewModel = TranslateViewModel(
            translateUseCase = TranslateUseCase(
                client = client,
                historyDataSource = errorDataSource
            ),
            historyDataSource = errorDataSource,
            coroutineScope = CoroutineScope(Dispatchers.Default)
        )

        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "test"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val resultState = awaitItem()
            assertThat(resultState.isTranslating).isFalse()
            assertThat(resultState.error).isEqualTo(errorDataSource.error)
        }
    }

    @Test
    fun `open and close from language dropdown`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            viewModel.onEvent(TranslateEvent.OpenFromLanguageDropdown)
            val openedDropdownState = awaitItem()
            assertThat(openedDropdownState.isChoosingFromLanguage).isTrue()
            assertThat(openedDropdownState.isChoosingToLanguage).isFalse()

            viewModel.onEvent(TranslateEvent.StopChoosingLanguage)
            val closedDropdownState = awaitItem()
            assertThat(closedDropdownState.isChoosingFromLanguage).isFalse()
            assertThat(closedDropdownState.isChoosingToLanguage).isFalse()
        }
    }

    @Test
    fun `open and close to language dropdown`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            viewModel.onEvent(TranslateEvent.OpenToLanguageDropdown)
            val openedDropdownState = awaitItem()
            assertThat(openedDropdownState.isChoosingToLanguage).isTrue()
            assertThat(openedDropdownState.isChoosingFromLanguage).isFalse()

            viewModel.onEvent(TranslateEvent.StopChoosingLanguage)
            val closedDropdownState = awaitItem()
            assertThat(closedDropdownState.isChoosingFromLanguage).isFalse()
            assertThat(closedDropdownState.isChoosingToLanguage).isFalse()
        }
    }

    @Test
    fun `choose from language`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            val fromUiLanguage = UiLanguage.byCode("tr")

            viewModel.onEvent(TranslateEvent.ChooseFromLanguage(fromUiLanguage))
            val fromLanguageChosenState = awaitItem()
            assertThat(fromLanguageChosenState.isChoosingFromLanguage).isFalse()
            assertThat(fromLanguageChosenState.fromLanguage).isEqualTo(fromUiLanguage)
        }
    }

    @Test
    fun `choose to language when translation empty`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            val toUiLanguage = UiLanguage.byCode("it")

            viewModel.onEvent(TranslateEvent.ChooseToLanguage(toUiLanguage))
            val toLanguageChosenState = awaitItem()
            assertThat(toLanguageChosenState.isChoosingToLanguage).isFalse()
            assertThat(toLanguageChosenState.toLanguage).isEqualTo(toUiLanguage)
        }
    }

    @Test
    fun `choose to language after applied translation`() = runBlocking {
        val toUiLanguage = UiLanguage.byCode("it")
        val updatedTranslatedText = "updated translation"

        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(TranslateState())

            // apply translation
            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "original text"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val translatedState = awaitItem()
            assertThat(translatedState.isTranslating).isFalse()
            assertThat(translatedState.toText).isEqualTo(client.translatedTest)

            // update toLanguage

            viewModel.onEvent(TranslateEvent.ChooseToLanguage(toUiLanguage))
            val toLanguageChosenState = awaitItem()
            assertThat(toLanguageChosenState.isChoosingToLanguage).isFalse()
            assertThat(toLanguageChosenState.toLanguage).isEqualTo(toUiLanguage)
            assertThat(toLanguageChosenState.isTranslating).isFalse()
            assertThat(toLanguageChosenState.toText).isEqualTo(client.translatedTest)

            client.translatedTest = updatedTranslatedText

            val translatingAgainState = awaitItem()
            assertThat(translatingAgainState.isTranslating).isTrue()

            val updatedTranslationState = awaitItem()
            assertThat(updatedTranslationState.isTranslating).isFalse()
            assertThat(updatedTranslationState.toText).isEqualTo(expected = client.translatedTest)

        }
    }

    @Test
    fun `swap languages when translation empty`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()

            viewModel.onEvent(TranslateEvent.SwapLanguages)
            val resultState = awaitItem()
            assertThat(resultState.fromLanguage).isEqualTo(initialState.toLanguage)
            assertThat(resultState.toLanguage).isEqualTo(initialState.fromLanguage)
        }
    }

    @Test
    fun `swap languages after translation applied`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(TranslateState())

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "test"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val translatedState = awaitItem()
            assertThat(translatedState.isTranslating).isFalse()
            assertThat(translatedState.toText).isEqualTo(client.translatedTest)

            viewModel.onEvent(TranslateEvent.SwapLanguages)
            val swappedState = awaitItem()
            assertThat(swappedState.fromLanguage).isEqualTo(initialState.toLanguage)
            assertThat(swappedState.toLanguage).isEqualTo(initialState.fromLanguage)

            assertThat(swappedState.fromText).isEqualTo(translatedState.toText)
            assertThat(swappedState.toText).isEqualTo(translatedState.fromText)
        }
    }

    @Test
    fun `select history item`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            val historyItem = HistoryItem(
                id = 0,
                fromLanguageCode = "fr",
                fromText = "original string",
                toLanguageCode = "tr",
                toText = "translated string"
            )
            dataSource.insertHistoryItem(item = historyItem)
            val insertedState = awaitItem()

            val expectedItem = UiHistoryItem(
                id = historyItem.id!!,
                fromText = historyItem.fromText,
                fromLanguage = UiLanguage.byCode(historyItem.fromLanguageCode),
                toText = historyItem.toText,
                toLanguage = UiLanguage.byCode(historyItem.toLanguageCode)
            )
            assertThat(insertedState.history.first()).isEqualTo(expected = expectedItem)

            viewModel.onEvent(TranslateEvent.SelectHistoryItem(item = insertedState.history.first()))
            val selectedItemState = awaitItem()

            assertThat(selectedItemState.isTranslating).isFalse()
            assertThat(selectedItemState.fromText).isEqualTo(expected = historyItem.fromText)
            assertThat(selectedItemState.fromLanguage).isEqualTo(
                expected = UiLanguage.byCode(
                    languageCode = historyItem.fromLanguageCode
                )
            )
            assertThat(selectedItemState.toText).isEqualTo(expected = historyItem.toText)
            assertThat(selectedItemState.toLanguage).isEqualTo(
                expected = UiLanguage.byCode(
                    languageCode = historyItem.toLanguageCode
                )
            )
        }
    }


    @Test
    fun `edit translation text`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "original text"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val translatedState = awaitItem()
            assertThat(translatedState.isTranslating).isFalse()
            assertThat(translatedState.toText).isEqualTo(client.translatedTest)

            viewModel.onEvent(TranslateEvent.EditTranslation)
            val editedState = awaitItem()
            assertThat(editedState.isTranslating).isFalse()
            assertThat(editedState.fromText).assertThat(translatedState.fromText)
            assertThat(editedState.toText).isNull()
        }
    }

    @Test
    fun `close translation after translation`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            viewModel.onEvent(TranslateEvent.ChangeTranslationText(text = "original text"))
            awaitItem()

            viewModel.onEvent(TranslateEvent.Translate)
            val loadingState = awaitItem()
            assertThat(loadingState.isTranslating).isTrue()

            val translatedState = awaitItem()
            assertThat(translatedState.isTranslating).isFalse()
            assertThat(translatedState.toText).isEqualTo(expected = client.translatedTest)

            viewModel.onEvent(TranslateEvent.CloseTranslation)
            val closedTranslationState = awaitItem()
            assertThat(closedTranslationState.isTranslating).isFalse()
            assertThat(closedTranslationState.fromText).isEqualTo(expected = initialState.fromText)
            assertThat(closedTranslationState.toText).isEqualTo(expected = initialState.toText)
        }
    }

    @Test
    fun `close translation after selecting history item`() = runBlocking {
        viewModel.state.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(expected = TranslateState())

            val historyItem = HistoryItem(
                id = 0,
                fromLanguageCode = "fr",
                fromText = "original string",
                toLanguageCode = "tr",
                toText = "translated string"
            )
            dataSource.insertHistoryItem(item = historyItem)
            val insertedState = awaitItem()
            assertThat(insertedState.history).isNotEmpty()

            viewModel.onEvent(TranslateEvent.SelectHistoryItem(item = insertedState.history.first()))
            awaitItem()

            viewModel.onEvent(TranslateEvent.CloseTranslation)
            val closedTranslationState = awaitItem()
            assertThat(closedTranslationState.isTranslating).isFalse()
            assertThat(closedTranslationState.fromText).isEqualTo(expected = initialState.fromText)
            assertThat(closedTranslationState.toText).isEqualTo(expected = initialState.toText)
        }
    }
}

