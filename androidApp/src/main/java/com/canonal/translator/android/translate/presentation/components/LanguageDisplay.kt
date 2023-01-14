package com.canonal.translator.android.translate.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.canonal.translator.android.core.theme.LightBlue
import com.canonal.translator.core.presentation.UiLanguage

@Composable
fun LanguageDisplay(
    uiLanguage: UiLanguage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallLanguageItem(uiLanguage = uiLanguage)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = uiLanguage.language.languageName,
            color = LightBlue,
            modifier = Modifier.testTag("LanguageDisplay")
        )
    }
}