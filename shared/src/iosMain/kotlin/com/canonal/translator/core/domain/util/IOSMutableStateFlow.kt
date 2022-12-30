package com.canonal.translator.core.domain.util

import kotlinx.coroutines.flow.MutableStateFlow

class IOSMutableStateFlow<T>(
    initialValue: T
) : CommonMutableStateFlow<T>(stateFlow = MutableStateFlow(initialValue))