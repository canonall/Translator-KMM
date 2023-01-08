package com.canonal.translator.voice_to_text.data

import com.canonal.translator.core.domain.util.CommonStateFlow
import com.canonal.translator.core.domain.util.toCommonStateFlow
import com.canonal.translator.voice_to_text.domain.VoiceToTextParser
import com.canonal.translator.voice_to_text.domain.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeVoiceToTextParser : VoiceToTextParser {

    var voiceResult = "test result"

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
        _state.update { voiceToTextParserState ->
            voiceToTextParserState.copy(
                result = voiceResult,
                isSpeaking = false
            )
        }
    }

    override fun cancel() = Unit

    override fun reset() {
        _state.update { VoiceToTextParserState() }
    }
}