package com.canonal.translator.core.domain.util

fun interface DisposableHandle : kotlinx.coroutines.DisposableHandle

// instead of block below, just use fun interface
//fun DisposableHandle(block: () -> Unit): DisposableHandle {
//    return object : DisposableHandle {
//        override fun dispose() {
//            block()
//        }
//    }
//}