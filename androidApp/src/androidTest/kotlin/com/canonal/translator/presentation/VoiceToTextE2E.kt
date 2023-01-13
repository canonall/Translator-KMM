package com.canonal.translator.presentation

import android.Manifest
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.canonal.translator.android.MainActivity
import com.canonal.translator.android.R
import com.canonal.translator.android.di.AppModule
import com.canonal.translator.android.voice_to_text.di.VoiceToTextModule
import com.canonal.translator.translate.data.remote.FakeTranslateClient
import com.canonal.translator.translate.domain.translate.TranslateClient
import com.canonal.translator.voice_to_text.data.FakeVoiceToTextParser
import com.canonal.translator.voice_to_text.domain.VoiceToTextParser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class, VoiceToTextModule::class)
class VoiceToTextE2E {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        // give permissions for the test class
        Manifest.permission.RECORD_AUDIO
    )

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Inject
    lateinit var fakeVoiceToTextParser: VoiceToTextParser

    @Inject
    lateinit var fakeClient: TranslateClient

    @Test
    fun recordAndTranslate() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parser = fakeVoiceToTextParser as FakeVoiceToTextParser
        val client = fakeClient as FakeTranslateClient

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.stop_recording))
            .performClick()

        parser.setVoiceResult()

        composeRule
            .onNodeWithText(parser.voiceResult)
            .assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.apply))
            .performClick()

        composeRule
            .onNodeWithText(parser.voiceResult)
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(context.getString(R.string.translate), ignoreCase = true)
            .performClick()

        composeRule
            .onNodeWithText(parser.voiceResult)
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(client.translatedTest)
            .assertIsDisplayed()
    }

    @Test
    fun recordAndError() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val errorParser = fakeVoiceToTextParser as FakeVoiceToTextParser

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.stop_recording))
            .performClick()

        errorParser.errorDuringListening()

        composeRule
            .onNodeWithText(errorParser.error)
            .assertIsDisplayed()
    }

    @Test
    fun recordAndCancel() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithText(context.getString(R.string.start_talking))
            .assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.stop_recording))
            .performClick()

        composeRule
            .onNodeWithText(context.getString(R.string.start_talking))
            .assertIsDisplayed()
    }

    @Test
    fun recordAndRefresh() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parser = fakeVoiceToTextParser as FakeVoiceToTextParser

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.stop_recording))
            .performClick()

        parser.setVoiceResult()

        composeRule
            .onNodeWithText(parser.voiceResult)
            .assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_again))
            .performClick()

        composeRule
            .onNodeWithTag("VoiceRecorderDisplay")
            .assertIsDisplayed()

        parser.setVoiceResultAfterRefresh()

        composeRule
            .onNodeWithText(parser.voiceResultAfterRefresh)
            .assertIsDisplayed()
    }

    @Test
    fun navigateBackWithXButton() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parser = fakeVoiceToTextParser as FakeVoiceToTextParser

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.record_audio))
            .performClick()

        val job = launch {
            delay(5000L)
            parser.setVoiceResult()
            composeRule
                .onNodeWithContentDescription(context.getString(R.string.apply))
                .performClick()
        }

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.close))
            .performClick()

        composeRule
            .onNodeWithText(parser.voiceResult)
            .assertDoesNotExist()

        job.cancel()
    }
}