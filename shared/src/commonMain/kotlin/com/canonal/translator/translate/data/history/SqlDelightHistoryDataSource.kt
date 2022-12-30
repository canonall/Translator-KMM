package com.canonal.translator.translate.data.history

import com.canonal.translator.core.domain.util.CommonFlow
import com.canonal.translator.core.domain.util.toCommonFlow
import com.canonal.translator.database.TranslateDatabase
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.history.HistoryItem
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class SqlDelightHistoryDataSource(
    // TranslateDatabase class is generated
    db: TranslateDatabase
) : HistoryDataSource {

    private val queries = db.translateQueries

    override fun getHistory(): CommonFlow<List<HistoryItem>> {
        return queries
            .getHistory()
            .asFlow()
            .mapToList()
            .map { historyEntityList ->
                historyEntityList.map { historyEntity ->
                    historyEntity.toHistoryItem()
                }
            }
            .toCommonFlow()
    }

    override suspend fun insertHistoryItem(item: HistoryItem) {
        queries.insertHistoryEntity(
            id = item.id,
            fromLanguageCode = item.fromLanguageCode,
            fromText = item.fromText,
            toLanguageCode = item.toLanguageCode,
            toText = item.toText,
            timestampt = Clock.System.now().toEpochMilliseconds()
        )
    }
}