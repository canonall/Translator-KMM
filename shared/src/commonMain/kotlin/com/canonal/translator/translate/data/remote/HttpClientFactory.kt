package com.canonal.translator.translate.data.remote

import io.ktor.client.*

expect class HttpClientFactory {
    fun create(): HttpClient
}