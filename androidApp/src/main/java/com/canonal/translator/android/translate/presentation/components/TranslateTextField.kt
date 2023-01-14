package com.canonal.translator.android.translate.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.canonal.translator.android.R
import com.canonal.translator.android.core.theme.LightBlue
import com.canonal.translator.core.presentation.UiLanguage

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TranslateTextField(
    fromText: String,
    toText: String?,
    isTranslating: Boolean,
    fromLanguage: UiLanguage,
    toLanguage: UiLanguage,
    onTranslateClick: () -> Unit,
    onTextChange: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    onTextFieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .gradientSurface()
            .clickable(onClick = onTextFieldClick)
            .padding(16.dp)
    ) {
        AnimatedContent(targetState = toText) { toText ->
            if (toText == null || isTranslating) {
                IdleTranslateTextField(
                    fromText = fromText,
                    isTranslating = isTranslating,
                    onTextChange = onTextChange,
                    onTranslateClick = onTranslateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .testTag("IdleTranslateTextField")
                )
            } else {
                TranslatedTextField(
                    fromText = fromText,
                    toText = toText,
                    fromLanguage = fromLanguage,
                    toLanguage = toLanguage,
                    onCopyClick = onCopyClick,
                    onCloseClick = onCloseClick,
                    onSpeakerClick = onSpeakerClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("TranslatedTextField")
                )
            }
        }
    }
}

@Composable
private fun TranslatedTextField(
    fromText: String,
    toText: String,
    fromLanguage: UiLanguage,
    toLanguage: UiLanguage,
    onCopyClick: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSpeakerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        LanguageDisplay(uiLanguage = fromLanguage)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = fromText,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.semantics {
                contentDescription = context.getString(R.string.original_text)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.align(Alignment.End)
        ) {
            IconButton(onClick = {
                onCopyClick(fromText)
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.copy),
                    contentDescription = stringResource(id = R.string.copy_from_translation),
                    tint = LightBlue
                )
            }
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(id = R.string.close),
                    tint = LightBlue
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        LanguageDisplay(uiLanguage = toLanguage)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = toText,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.semantics {
                contentDescription = context.getString(R.string.translated_text)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.align(Alignment.End)
        ) {
            IconButton(onClick = {
                onCopyClick(toText)
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.copy),
                    contentDescription = stringResource(id = R.string.copy_to_translation),
                    tint = LightBlue
                )
            }
            IconButton(onClick = onSpeakerClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.speaker),
                    contentDescription = stringResource(id = R.string.play_loud),
                    tint = LightBlue
                )
            }
        }
    }
}

@Composable
private fun IdleTranslateTextField(
    fromText: String,
    isTranslating: Boolean,
    onTextChange: (String) -> Unit,
    onTranslateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = modifier) {
        BasicTextField(
            value = fromText,
            onValueChange = onTextChange,
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged { isFocused = it.isFocused }
                .semantics {
                    contentDescription =
                        context.getString(R.string.idle_textfield_content_description)
                },
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface
            )
        )
        if (fromText.isEmpty() && !isFocused) {
            Text(
                text = stringResource(id = R.string.enter_a_text_to_translate),
                color = LightBlue
            )
        }
        ProgressButton(
            text = stringResource(id = R.string.translate),
            isLoading = isTranslating,
            onClick = onTranslateClick,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}