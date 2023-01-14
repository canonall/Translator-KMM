package com.canonal.translator.presentation

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.canonal.translator.android.MainActivity
import com.canonal.translator.android.R
import com.canonal.translator.android.di.AppModule
import com.canonal.translator.android.voice_to_text.di.VoiceToTextModule
import com.canonal.translator.core.presentation.UiLanguage
import com.canonal.translator.translate.data.local.FakeHistoryDataSource
import com.canonal.translator.translate.data.remote.FakeTranslateClient
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.history.HistoryItem
import com.canonal.translator.translate.domain.translate.TranslateClient
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class, VoiceToTextModule::class)
class TranslateE2E {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Inject
    lateinit var fakeClient: TranslateClient

    @Inject
    lateinit var fakeHistoryDataSource: HistoryDataSource

    @Test
    fun typeAndTranslate() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = fakeClient as FakeTranslateClient
        val translationString = "translation string"

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.idle_textfield_content_description))
            .performTextInput(translationString)

        composeRule
            .onNodeWithText(context.getString(R.string.translate), ignoreCase = true)
            .performClick()

        composeRule
            .onNodeWithText(translationString)
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(client.translatedTest)
            .assertIsDisplayed()
    }

    @Test
    fun changeLanguageAfterTranslationAndUpdate() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = fakeClient as FakeTranslateClient

        // apply a translation

        typeAndTranslate()

        // change toLanguage from dropdown menu

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.to_language_dropdown_content_description))
            .performClick()

        client.translatedTest = "updated translation"

        composeRule
            .onNodeWithTag("tr")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithText(client.translatedTest)
            .assertIsDisplayed()
    }

    @Test
    fun hideTranslatedTextOnEditTranslation() = runTest {
        // apply a translation

        typeAndTranslate()

        // click TranslateTextField
        // TranslatedTextField should be not visible

        composeRule
            .onNodeWithTag("TranslateTextField")
            .performClick()

        composeRule
            .onNodeWithTag("TranslatedTextField")
            .assertDoesNotExist()
    }

    @Test
    fun hideTranslatedTestOnCloseClick() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()

        typeAndTranslate()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.close))
            .performClick()

        composeRule
            .onNodeWithText(context.getString(R.string.enter_a_text_to_translate))
            .assertIsDisplayed()

        composeRule
            .onNodeWithTag("TranslatedTextField")
            .assertDoesNotExist()
    }

    @Test
    fun changeLanguagesFromDropDownMenu() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val fromLanguage = "French"
        val toLanguage = "Turkish"

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.from_language_dropdown_content_description))
            .performClick()

        composeRule
            .onNodeWithTag("fr")
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.from_language_dropdown_content_description))
            .onChild()
            .assertIsDisplayed()
            .assertTextEquals(fromLanguage)

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.to_language_dropdown_content_description))
            .performClick()

        composeRule
            .onNodeWithTag("tr")
            .performScrollTo()
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.to_language_dropdown_content_description))
            .onChild()
            .assertIsDisplayed()
            .assertTextEquals(toLanguage)
    }

    @Test
    fun swapLanguages() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val afterSwapFromLanguage = "German"
        val afterSwapToLanguage = "English"

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.swap_languages))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.from_language_dropdown_content_description))
            .onChild()
            .assertIsDisplayed()
            .assertTextEquals(afterSwapFromLanguage)

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.to_language_dropdown_content_description))
            .onChild()
            .assertIsDisplayed()
            .assertTextEquals(afterSwapToLanguage)
    }

    @Test
    fun addHistoryItem() = runTest {
        val client = fakeClient as FakeTranslateClient
        val historyDataSource = fakeHistoryDataSource as FakeHistoryDataSource
        val translationString = "translation string"

        // no history item is visible when no translation has done before

        composeRule
            .onNodeWithTag("TranslateHistoryItem")
            .assertDoesNotExist()

        // apply a translation

        typeAndTranslate()

        val historyItem = HistoryItem(
            id = 0,
            fromLanguageCode = "en",
            fromText = translationString,
            toLanguageCode = "de",
            toText = client.translatedTest
        )
        historyDataSource.insertHistoryItem(item = historyItem)

        composeRule
            .onNodeWithTag(historyItem.id.toString())
            .assertIsDisplayed()
    }

    @Test
    fun selectHistoryItemFromPreviousTranslation() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val client = fakeClient as FakeTranslateClient
        val historyDataSource = fakeHistoryDataSource as FakeHistoryDataSource
        val translationString = "translation string"

        // previously applied translation

        val historyItem = HistoryItem(
            id = 0,
            fromLanguageCode = "en",
            fromText = translationString,
            toLanguageCode = "de",
            toText = client.translatedTest
        )
        historyDataSource.insertHistoryItem(item = historyItem)

        // initially translation is empty

        composeRule
            .onNodeWithText(context.getString(R.string.enter_a_text_to_translate))
            .assertIsDisplayed()

        // click to previously added history item

        composeRule
            .onNodeWithTag(historyItem.id.toString())
            .performClick()

        // verify ui changes

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.from_language_dropdown_content_description))
            .onChild()
            .assertTextEquals(UiLanguage.byCode(historyItem.fromLanguageCode).language.languageName)

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.to_language_dropdown_content_description))
            .onChild()
            .assertTextEquals(UiLanguage.byCode(historyItem.toLanguageCode).language.languageName)

        composeRule
            .onAllNodesWithTag("LanguageDisplay", useUnmergedTree = true)
            .onFirst()
            .assertTextEquals(UiLanguage.byCode(historyItem.fromLanguageCode).language.languageName)

        composeRule
            .onAllNodesWithTag("LanguageDisplay", useUnmergedTree = true)
            .onLast()
            .assertTextEquals(UiLanguage.byCode(historyItem.toLanguageCode).language.languageName)

        composeRule
            .onNodeWithContentDescription(
                context.getString(R.string.original_text),
                useUnmergedTree = true
            )
            .assertTextEquals(historyItem.fromText)

        composeRule
            .onNodeWithContentDescription(
                context.getString(R.string.translated_text),
                useUnmergedTree = true
            )
            .assertTextEquals(historyItem.toText)
    }
}