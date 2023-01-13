package com.canonal.translator.voice_to_text.data

import com.canonal.translator.core.domain.util.CommonStateFlow
import com.canonal.translator.core.domain.util.toCommonStateFlow
import com.canonal.translator.voice_to_text.domain.VoiceToTextParser
import com.canonal.translator.voice_to_text.domain.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeVoiceToTextParser : VoiceToTextParser {

    var voiceResult = "test result"
    var voiceResultAfterRefresh = "test result after refresh"
    var error = "Speech recognition is failed due to an error."

    private val _state = MutableStateFlow(VoiceToTextParserState())
    override val state: CommonStateFlow<VoiceToTextParserState>
        get() = _state.toCommonStateFlow()

    override fun startListening(languageCode: String) {
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(
                result = "",
                isSpeaking = true
            )
        }
    }

    override fun stopListening() {
        _state.update { VoiceToTextParserState() }
    }

    override fun cancel() = Unit

    override fun reset() {
        _state.update { VoiceToTextParserState() }
    }

    fun errorDuringListening() {
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(
                error = error
            )
        }
    }

    fun setVoiceResult() {
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(
                result = voiceResult,
                isSpeaking = false
            )
        }
    }

    fun setVoiceResultAfterRefresh() {
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(
                result = voiceResultAfterRefresh,
                isSpeaking = false
            )
        }
    }
}