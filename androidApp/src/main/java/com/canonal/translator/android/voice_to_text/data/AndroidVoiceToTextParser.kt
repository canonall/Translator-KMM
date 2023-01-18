package com.canonal.translator.android.voice_to_text.data

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.ERROR_CLIENT
import android.util.Log
import com.canonal.translator.R
import com.canonal.translator.core.domain.util.CommonStateFlow
import com.canonal.translator.core.domain.util.toCommonStateFlow
import com.canonal.translator.voice_to_text.domain.VoiceToTextParser
import com.canonal.translator.voice_to_text.domain.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AndroidVoiceToTextParser(
    private val application: Application
) : VoiceToTextParser, RecognitionListener {

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(application)

    private val _state = MutableStateFlow(VoiceToTextParserState())
    override val state: CommonStateFlow<VoiceToTextParserState>
        get() = _state.toCommonStateFlow()

    override fun startListening(languageCode: String) {
        _state.update { VoiceToTextParserState() }

        if (!SpeechRecognizer.isRecognitionAvailable(application)) {
            _state.update { voiceToTextParserState ->
                voiceToTextParserState.copy(
                    error = application.getString(R.string.error_speech_recognition_unavailable)
                )
            }
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }
        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(
                isSpeaking = true
            )
        }
    }

    override fun stopListening() {
        _state.update { VoiceToTextParserState() }
        recognizer.stopListening()
    }

    override fun cancel() {
        recognizer.cancel()
    }

    override fun reset() {
        _state.value = VoiceToTextParserState()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(error = null)
        }
    }

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(rmsdB: Float) {
        // convert the rmsdB to a value between 0 and 1
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(powerRatio = rmsdB * (1f / (12f - 2f)))
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(isSpeaking = false)
        }
    }

    override fun onError(code: Int) {
        // ignore error_client (5) because
        // API returns an error when stopping voice-to-text
        if (code == ERROR_CLIENT) {
            return
        }
        _state.update { voiceToTextParserState ->
            Log.d("AndroidVoiceToTextParser", "onError: $code")
            voiceToTextParserState.copy(error = application.getString(R.string.error_speech_recognition_failed))
        }
    }

    override fun onResults(results: Bundle?) {
        // actual result is in the first item
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { text ->
                _state.update { voiceToTextParserState ->
                    voiceToTextParserState.copy(result = text)
                }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    override fun onEvent(eventType: Int, params: Bundle?) = Unit
}
