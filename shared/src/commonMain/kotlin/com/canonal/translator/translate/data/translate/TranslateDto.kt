package com.canonal.translator.translate.data.translate

import kotlinx.serialization.SerialName

// attach this object to the request
@kotlinx.serialization.Serializable
data class TranslateDto(
    // @SerialName("apiField") val customName
    @SerialName("q")
    val textToTranslate: String,
    @SerialName("source")
    val sourceLanguageCode: String,
    @SerialName("target")
    val targetLanguageCode: String
)
