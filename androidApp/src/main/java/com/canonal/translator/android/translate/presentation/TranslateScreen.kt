package com.canonal.translator.android.translate.presentation

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.canonal.translator.R
import com.canonal.translator.android.destinations.VoiceToTextScreenDestination
import com.canonal.translator.android.translate.presentation.components.*
import com.canonal.translator.translate.domain.translate.TranslateError
import com.canonal.translator.translate.presentation.TranslateEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun TranslateScreen(
    viewModel: AndroidTranslateViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<VoiceToTextScreenDestination, String>
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // triggered whenever a certain piece of state changes
    LaunchedEffect(key1 = state.error) {
        val message = when (state.error) {
            TranslateError.SERVICE_UNAVAILABLE -> context.getString(R.string.error_service_unavailable)
            TranslateError.CLIENT_ERROR -> context.getString(R.string.client_error)
            TranslateError.SERVER_ERROR -> context.getString(R.string.server_error)
            TranslateError.UNKNOWN_ERROR -> context.getString(R.string.unknown_error)
            else -> null
        }
        message?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            // reset error message after it is shown
            viewModel.onEvent(event = TranslateEvent.OnErrorSeen)
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                viewModel.onEvent(event = TranslateEvent.SubmitVoiceResult(result = result.value))
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigator.navigate(
                        VoiceToTextScreenDestination(
                            languageCode = state.fromLanguage.language.languageCode
                        )
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(75.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = com.canonal.translator.android.R.drawable.mic),
                    contentDescription = stringResource(id = R.string.record_audio)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LanguageDropDown(
                        uiLanguage = state.fromLanguage,
                        isOpen = state.isChoosingFromLanguage,
                        onClick = {
                            viewModel.onEvent(event = TranslateEvent.OpenFromLanguageDropdown)
                        },
                        onDismiss = {
                            viewModel.onEvent(event = TranslateEvent.StopChoosingLanguage)
                        },
                        onSelectLanguage = { uiLanguage ->
                            viewModel.onEvent(event = TranslateEvent.ChooseFromLanguage(uiLanguage = uiLanguage))
                        },
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.from_language_dropdown_content_description)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SwapLanguagesButton(onClick = {
                        viewModel.onEvent(event = TranslateEvent.SwapLanguages)
                    })
                    Spacer(modifier = Modifier.weight(1f))
                    LanguageDropDown(
                        uiLanguage = state.toLanguage,
                        isOpen = state.isChoosingToLanguage,
                        onClick = {
                            viewModel.onEvent(event = TranslateEvent.OpenToLanguageDropdown)
                        },
                        onDismiss = {
                            viewModel.onEvent(event = TranslateEvent.StopChoosingLanguage)
                        },
                        onSelectLanguage = { uiLanguage ->
                            viewModel.onEvent(event = TranslateEvent.ChooseToLanguage(uiLanguage = uiLanguage))
                        },
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.to_language_dropdown_content_description)
                        }
                    )
                }
            }
            item {
                val clipboardManager = LocalClipboardManager.current
                val keyboardController = LocalSoftwareKeyboardController.current
                val textToSpeech = rememberTextToSpeech()

                TranslateTextField(
                    fromText = state.fromText,
                    toText = state.toText,
                    isTranslating = state.isTranslating,
                    fromLanguage = state.fromLanguage,
                    toLanguage = state.toLanguage,
                    onTranslateClick = {
                        keyboardController?.hide()
                        viewModel.onEvent(event = TranslateEvent.Translate)
                    },
                    onTextChange = { text ->
                        viewModel.onEvent(event = TranslateEvent.ChangeTranslationText(text))
                    },
                    onCopyClick = { text ->
                        clipboardManager.setText(
                            buildAnnotatedString {
                                append(text)
                            }
                        )
                        Toast.makeText(
                            context,
                            context.getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onCloseClick = {
                        viewModel.onEvent(event = TranslateEvent.CloseTranslation)
                    },
                    onSpeakerClick = {
                        textToSpeech.language = state.toLanguage.toLocale() ?: Locale.ENGLISH
                        textToSpeech.speak(
                            state.toText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    },
                    onTextFieldClick = {
                        viewModel.onEvent(event = TranslateEvent.EditTranslation)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("TranslateTextField")
                )
            }
            item {
                if (state.history.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.history),
                        style = MaterialTheme.typography.h2
                    )
                }
            }
            items(state.history) { item ->
                TranslateHistoryItem(
                    uiHistoryItem = item,
                    onClick = {
                        viewModel.onEvent(event = TranslateEvent.SelectHistoryItem(item))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(item.id.toString())
                )
            }
        }
    }
}
