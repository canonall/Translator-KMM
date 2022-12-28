package com.canonal.translator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform