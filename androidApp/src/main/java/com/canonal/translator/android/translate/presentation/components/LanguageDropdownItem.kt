package com.canonal.translator.android.translate.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.canonal.translator.core.presentation.UiLanguage

@Composable
fun LanguageDropdownItem(
    uiLanguage: UiLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        onClick = onClick,
        modifier = modifier.testTag(uiLanguage.language.languageCode)
    ) {
        Image(
            painter = painterResource(id = uiLanguage.drawableRes),
            contentDescription = uiLanguage.language.languageName,
            modifier = modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = uiLanguage.language.languageName)
    }
}
