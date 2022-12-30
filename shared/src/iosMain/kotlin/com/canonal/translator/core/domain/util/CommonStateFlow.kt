package com.canonal.translator.core.domain.util

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

actual open class CommonStateFlow<T> actual constructor(
    private val stateFlow: StateFlow<T>
) : CommonFlow<T>(flow = stateFlow), StateFlow<T> {
    override val replayCache: List<T>
        get() = stateFlow.replayCache

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        stateFlow.collect(collector)
    }

    override val value: T
        get() = stateFlow.value
}