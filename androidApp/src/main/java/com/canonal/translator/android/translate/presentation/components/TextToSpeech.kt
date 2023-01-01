package com.canonal.translator.android.translate.presentation.components

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// as soon as the composable leaves the composition
// it will also dispose the object properly
@Composable
fun rememberTextToSpeech(): TextToSpeech {
    val context = LocalContext.current
    val textToSpeech = remember { TextToSpeech(context, null) }

    DisposableEffect(key1 = textToSpeech) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
    return  textToSpeech
}
