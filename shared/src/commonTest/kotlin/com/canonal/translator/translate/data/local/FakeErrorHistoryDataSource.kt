package com.canonal.translator.translate.data.local

import com.canonal.translator.core.domain.util.CommonFlow
import com.canonal.translator.core.domain.util.toCommonFlow
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.history.HistoryItem
import com.canonal.translator.translate.domain.translate.TranslateError
import com.canonal.translator.translate.domain.translate.TranslateException
import kotlinx.coroutines.flow.MutableStateFlow

class FakeErrorHistoryDataSource: HistoryDataSource {

    var error = TranslateError.UNKNOWN_ERROR

    private val _data = MutableStateFlow<List<HistoryItem>>(emptyList())

    override fun getHistory(): CommonFlow<List<HistoryItem>> {
        return _data.toCommonFlow()
    }

    override suspend fun insertHistoryItem(item: HistoryItem) {
        throw TranslateException(error = error)
    }
}