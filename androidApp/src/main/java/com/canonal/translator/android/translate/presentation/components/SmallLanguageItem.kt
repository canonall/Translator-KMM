package com.canonal.translator.android.translate.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.canonal.translator.core.presentation.UiLanguage

@Composable
fun SmallLanguageItem(
    uiLanguage: UiLanguage,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = uiLanguage.drawableRes,
        contentDescription = uiLanguage.language.languageName,
        modifier = modifier.size(25.dp)
    )
}
